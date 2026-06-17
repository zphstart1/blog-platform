package com.blog.comment.interfaces;

import com.blog.comment.application.CommentApplicationService;
import com.blog.comment.domain.Comment;
import com.blog.dto.PageResult;
import com.blog.shared.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentApplicationService commentApplicationService;

    @GetMapping("/articles/{articleId}/comments")
    public Result<PageResult<Comment>> list(@PathVariable Long articleId,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "20") int size) {
        return commentApplicationService.listByArticle(articleId, page, size);
    }

    @PostMapping("/articles/{articleId}/comments")
    public Result<Object> submit(@PathVariable Long articleId,
                                  @RequestBody Map<String, Object> req,
                                  HttpServletRequest request) {
        return commentApplicationService.submit(
                articleId,
                (String) req.get("content"),
                toLong(req.get("parentId")),
                toLong(req.get("replyToId")),
                (String) req.get("authorName"),
                (String) req.get("authorEmail"),
                (String) req.get("authorWebsite"),
                request.getRemoteAddr(),
                request.getHeader("User-Agent")
        );
    }

    @GetMapping("/admin/comments/pending")
    public Result<PageResult<Comment>> pending(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "20") int size) {
        return commentApplicationService.listPending(page, size);
    }

    @PutMapping("/admin/comments/{id}/review")
    public Result<Void> review(@PathVariable Long id, @RequestBody Map<String, String> req) {
        return commentApplicationService.review(id, req.get("action"));
    }

    @DeleteMapping("/admin/comments/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return commentApplicationService.delete(id);
    }

    private static Long toLong(Object v) {
        if (v instanceof Number) return ((Number) v).longValue();
        if (v instanceof String) {
            try { return Long.parseLong((String) v); } catch (NumberFormatException e) { return null; }
        }
        return null;
    }
}
