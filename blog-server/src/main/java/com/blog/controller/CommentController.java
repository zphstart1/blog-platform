package com.blog.controller;

import com.blog.common.Ret;
import com.blog.dto.CommentCreateRequest;
import com.blog.dto.PageRequest;
import com.blog.dto.PageResult;
import com.blog.dto.ReviewRequest;
import com.blog.service.CommentService;
import com.blog.vo.CommentVO;
import com.blog.vo.PendingCommentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 评论接口 — 评论提交与列表（部分鉴权）
 */
@Slf4j
@Api(tags = "评论接口")
@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @ApiOperation("文章评论列表（公开）")
    @GetMapping("/articles/{articleId}/comments")
    public Ret<PageResult<CommentVO>> list(@PathVariable Long articleId,
                                            PageRequest request,
                                            @RequestParam(defaultValue = "createdAt") String sort) {
        return commentService.commentList(articleId, request, sort);
    }

    @ApiOperation("提交评论（访客+注册用户）")
    @PostMapping("/articles/{articleId}/comments")
    public Ret<CommentVO> submit(@PathVariable Long articleId,
                                  @Valid @RequestBody CommentCreateRequest request,
                                  HttpServletRequest httpRequest) {
        String ip = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        return commentService.submitComment(articleId, request, ip, userAgent);
    }

    @ApiOperation("待审核评论列表（管理端）")
    @GetMapping("/admin/comments/pending")
    public Ret<PageResult<PendingCommentVO>> pending(PageRequest request) {
        return commentService.pendingCommentPage(request);
    }

    @ApiOperation("审核评论（管理端）")
    @PutMapping("/admin/comments/{id}/review")
    public Ret<Void> review(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        return commentService.reviewComment(id, request);
    }

    @ApiOperation("删除评论（管理端）")
    @DeleteMapping("/admin/comments/{id}")
    public Ret<Void> delete(@PathVariable Long id) {
        return commentService.deleteComment(id);
    }

    /**
     * 获取客户端真实IP — 优先从 X-Forwarded-For / X-Real-IP 获取
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}