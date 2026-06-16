package com.blog.service;

import com.blog.common.*;
import com.blog.config.CurrentUser;
import com.blog.config.RateLimitProperties;
import com.blog.dto.CommentCreateRequest;
import com.blog.dto.PageRequest;
import com.blog.dto.PageResult;
import com.blog.dto.ReviewRequest;
import com.blog.entity.Comment;
import com.blog.mapper.CommentMapper;
import com.blog.utils.XssUtil;
import com.blog.vo.CommentVO;
import com.blog.vo.PendingCommentVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 评论服务 — 提交/列表/审核/嵌套回复/频率限制
 * 访客评论先审后发(PENDING)，注册用户评论先发后审(APPROVED)
 */
@Slf4j
@Service
public class CommentService {

    private final CommentMapper commentMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RateLimitProperties rateLimitProperties;

    public CommentService(CommentMapper commentMapper, StringRedisTemplate stringRedisTemplate,
                          RedisTemplate<String, Object> redisTemplate, RateLimitProperties rateLimitProperties) {
        this.commentMapper = commentMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisTemplate = redisTemplate;
        this.rateLimitProperties = rateLimitProperties;
    }

    /**
     * 提交评论 — 访客先审后发，注册用户先发后审
     */
    public Ret<CommentVO> submitComment(Long articleId, CommentCreateRequest request, String ip, String userAgent) {
        CurrentUser currentUser = CurrentUser.get();

        // 频率限制检查
        if (currentUser != null) {
            // 注册用户频率限制
            String rateKey = "blr:comment:user:" + currentUser.getId();
            if (!checkRateLimit(rateKey, rateLimitProperties.getCommentUserPerMinute())) {
                return Ret.fail(ErrorCode.COMMENT_TOO_FREQUENT, "评论过于频繁，请60秒后重试");
            }
        } else {
            // 访客IP频率限制
            String rateKey = "blr:comment:ip:" + ip;
            if (!checkRateLimit(rateKey, rateLimitProperties.getCommentVisitorPerMinute())) {
                return Ret.fail(ErrorCode.COMMENT_TOO_FREQUENT, "评论过于频繁，请60秒后重试");
            }
        }

        // XSS 过滤 — 评论内容只保留纯文本
        String safeContent = XssUtil.stripHtmlTags(XssUtil.escapeHtml(request.getContent()));

        // 创建评论
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setUserId(currentUser != null ? currentUser.getId() : null);
        comment.setParentId(request.getParentId());
        comment.setReplyToId(request.getParentId());
        comment.setAuthorName(XssUtil.escapeHtml(request.getAuthorName()));
        comment.setAuthorEmail(request.getAuthorEmail());
        comment.setAuthorWebsite(request.getAuthorWebsite());
        comment.setContent(safeContent);
        comment.setIp(ip);
        comment.setUserAgent(userAgent);

        // 审核策略：访客 PENDING，注册用户 APPROVED
        if (currentUser != null) {
            comment.setStatus(CommentStatus.APPROVED.getCode());
        } else {
            comment.setStatus(CommentStatus.PENDING.getCode());
        }

        commentMapper.insert(comment);

        // 注册用户评论直接展示，删除该文章评论缓存
        if (currentUser != null && CommentStatus.APPROVED.getCode().equals(comment.getStatus())) {
            deleteCommentCache(articleId);
        }

        String message = CommentStatus.PENDING.getCode().equals(comment.getStatus())
                ? "评论已提交，审核通过后展示" : "评论成功";
        log.info("提交评论: id={}, articleId={}, userId={}, status={}", comment.getId(), articleId,
                currentUser != null ? currentUser.getId() : null, comment.getStatus());

        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setStatus(comment.getStatus());
        return Ret.ok(message, vo);
    }

    /**
     * 文章评论列表 — 只返回 APPROVED 状态，含嵌套回复
     */
    public Ret<PageResult<CommentVO>> commentList(Long articleId, PageRequest request, String sort) {
        // Cache-Aside: 先查缓存
        String cacheKey = "blg:comment:list:" + articleId + ":page:" + request.getPage();
        PageResult<CommentVO> cached = (PageResult<CommentVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return Ret.ok(cached);
        }

        Page<?> page = new Page<>(request.getPage(), request.getSize());
        IPage<CommentVO> result = commentMapper.selectApprovedCommentPage(page, articleId, sort);

        // 补充子回复
        for (CommentVO parent : result.getRecords()) {
            parent.setChildren(findChildren(parent.getId(), articleId));
        }

        PageResult<CommentVO> pageResult = PageResult.of(result.getRecords(), result.getTotal(),
                request.getPage(), request.getSize());

        redisTemplate.opsForValue().set(cacheKey, pageResult, 600 + (long)(Math.random() * 120), TimeUnit.SECONDS);
        return Ret.ok(pageResult);
    }

    /**
     * 待审核评论列表 — 审核队列
     */
    public Ret<PageResult<PendingCommentVO>> pendingCommentPage(PageRequest request) {
        Page<?> page = new Page<>(request.getPage(), request.getSize());
        IPage<PendingCommentVO> result = commentMapper.selectPendingCommentPage(page);
        PageResult<PendingCommentVO> pageResult = PageResult.of(result.getRecords(), result.getTotal(),
                request.getPage(), request.getSize());
        return Ret.ok(pageResult);
    }

    /**
     * 审核评论 — APPROVE 或 REJECT
     */
    public Ret<Void> reviewComment(Long id, ReviewRequest request) {
        CurrentUser currentUser = CurrentUser.get();
        if (!currentUser.canReview()) {
            return Ret.forbidden("无审核权限");
        }

        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            return Ret.notFound("评论不存在");
        }
        if (!CommentStatus.PENDING.getCode().equals(comment.getStatus())) {
            return Ret.badRequest("评论不在待审核状态");
        }

        ReviewAction action = ReviewAction.valueOf(request.getAction());
        comment.setStatus(action == ReviewAction.APPROVE ? CommentStatus.APPROVED.getCode() : CommentStatus.REJECTED.getCode());
        commentMapper.updateById(comment);

        // 删除该文章评论缓存
        deleteCommentCache(comment.getArticleId());

        log.info("审核评论: id={}, action={}, reviewerId={}", id, action.getCode(), currentUser.getId());
        return Ret.ok(action == ReviewAction.APPROVE ? "审核通过" : "已拒绝", null);
    }

    /**
     * 删除评论 — 物理删除
     */
    public Ret<Void> deleteComment(Long id) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            return Ret.notFound("评论不存在");
        }

        // 同时删除所有子回复
        LambdaQueryWrapper<Comment> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(Comment::getParentId, id);
        commentMapper.delete(childWrapper);

        commentMapper.deleteById(id);

        // 删除缓存
        deleteCommentCache(comment.getArticleId());

        log.info("删除评论: id={}, articleId={}", id, comment.getArticleId());
        return Ret.ok("删除成功", null);
    }

    // ========== 私有方法 ==========

    /**
     * 频率限制检查 — Redis INCR + EXPIRE
     */
    private boolean checkRateLimit(String key, int limit) {
        try {
            Long count = stringRedisTemplate.opsForValue().increment(key);
            if (count != null && count == 1) {
                stringRedisTemplate.expire(key, 60, TimeUnit.SECONDS);
            }
            return count == null || count <= limit;
        } catch (Exception e) {
            log.warn("频率限制检查失败(Redis可能不可用): {}", e.getMessage());
            return true; // Redis故障时降级为不限制
        }
    }

    /**
     * 查找子回复 — 最多2层嵌套
     */
    private List<CommentVO> findChildren(Long parentId, Long articleId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getArticleId, articleId);
        wrapper.eq(Comment::getParentId, parentId);
        wrapper.eq(Comment::getStatus, CommentStatus.APPROVED.getCode());
        wrapper.orderByAsc(Comment::getCreatedAt);

        List<Comment> children = commentMapper.selectList(wrapper);
        List<CommentVO> voList = new ArrayList<>();
        for (Comment child : children) {
            CommentVO vo = toCommentVO(child);
            vo.setChildren(new ArrayList<>()); // 只返回2层，不再递归
            voList.add(vo);
        }
        return voList;
    }

    private CommentVO toCommentVO(Comment comment) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setArticleId(comment.getArticleId());
        vo.setParentId(comment.getParentId());
        vo.setReplyToId(comment.getReplyToId());
        vo.setAuthorName(comment.getAuthorName());
        vo.setAuthorWebsite(comment.getAuthorWebsite());
        vo.setContent(comment.getContent());
        vo.setStatus(comment.getStatus());
        vo.setCreatedAt(comment.getCreatedAt());
        return vo;
    }

    private void deleteCommentCache(Long articleId) {
        try {
            // 删除所有页的评论缓存
            java.util.Set<String> keys = redisTemplate.keys("blg:comment:list:" + articleId + ":*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.warn("评论缓存删除失败: {}", e.getMessage());
        }
    }
}