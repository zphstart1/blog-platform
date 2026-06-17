package com.blog.comment.application;

import com.blog.comment.domain.*;
import com.blog.dto.PageResult;
import com.blog.shared.AuthContext;
import com.blog.shared.Result;
import com.blog.utils.XssUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentApplicationService {

    private final CommentRepository commentRepository;
    private final StringRedisTemplate stringRedisTemplate;

    public Result<Object> submit(Long articleId, String content, Long parentId, Long replyToId,
                                  String authorName, String authorEmail, String authorWebsite,
                                  String ip, String userAgent) {
        AuthContext.LoginUser currentUser = AuthContext.get();

        // 频率限制
        String rateKey = currentUser != null
                ? "blr:comment:user:" + currentUser.getId()
                : "blr:comment:ip:" + ip;
        Long count = stringRedisTemplate.opsForValue().increment(rateKey);
        if (count == 1) stringRedisTemplate.expire(rateKey, 60, TimeUnit.SECONDS);
        if (count != null && count > (currentUser != null ? 10 : 3)) {
            return Result.fail(429, "评论过于频繁，请60秒后重试");
        }

        // XSS 过滤
        String safeContent = XssUtil.stripHtmlTags(XssUtil.escapeHtml(content));

        boolean isGuest = currentUser == null;
        Comment comment = Comment.submit(
                articleId,
                currentUser != null ? currentUser.getId() : null,
                parentId, replyToId,
                isGuest ? authorName : currentUser.getUsername(),
                authorEmail, authorWebsite,
                CommentContent.of(safeContent),
                isGuest, userAgent, ip
        );

        commentRepository.save(comment);

        log.info("{}提交评论: articleId={}, status={}", isGuest ? "访客" : "用户", articleId, comment.getStatus().getCode());

        Map<String, Object> data = new HashMap<>();
        data.put("id", comment.getId());
        data.put("status", comment.getStatus().getCode());
        data.put("message", isGuest ? "评论已提交，等待审核" : "评论成功");
        return Result.ok(data);
    }

    public Result<PageResult<Comment>> listByArticle(Long articleId, int page, int size) {
        List<Comment> comments = commentRepository.findByArticleId(articleId, page, size);
        long total = commentRepository.countByArticleId(articleId);
        return Result.ok(PageResult.of(comments, total, page, size));
    }

    public Result<PageResult<Comment>> listPending(int page, int size) {
        List<Comment> records = commentRepository.findPending(page, size);
        long total = commentRepository.countPending();
        return Result.ok(PageResult.of(records, total, page, size));
    }

    public Result<Void> review(Long commentId, String action) {
        Comment comment = commentRepository.findById(CommentId.of(commentId)).orElse(null);
        if (comment == null) return Result.fail(404, "评论不存在");

        if (action == null) return Result.fail(400, "审核操作不能为空");
        String act = action.toLowerCase();
        if ("approve".equals(act)) comment.approve();
        else if ("reject".equals(act)) comment.reject();
        else return Result.fail(400, "无效的审核操作");

        commentRepository.save(comment);
        log.info("审核评论: id={}, action={}", commentId, action);
        return Result.ok();
    }

    public Result<Void> delete(Long commentId) {
        commentRepository.delete(CommentId.of(commentId));
        return Result.ok();
    }
}
