package com.blog.comment.domain;

import com.blog.shared.BaseEntity;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 评论聚合根 — comment 限界上下文
 */
@Getter
public class Comment extends BaseEntity {

    private Long articleId;  // 关联文章ID（跨Context用原始ID）
    private Long userId;     // 关联用户ID
    private Long parentId;   // 父评论ID
    private Long replyToId;  // 回复目标用户ID
    private String authorName;
    private String authorEmail;
    private String authorWebsite;
    private CommentContent content;
    private CommentStatus status;
    private String userAgent;
    private String ip;

    protected Comment() {}

    public static Comment submit(Long articleId, Long userId, Long parentId, Long replyToId,
                                  String authorName, String authorEmail, String authorWebsite,
                                  CommentContent content, boolean isGuest, String userAgent, String ip) {
        Comment c = new Comment();
        c.articleId = articleId;
        c.userId = userId;
        c.parentId = parentId;
        c.replyToId = replyToId;
        c.authorName = authorName;
        c.authorEmail = authorEmail;
        c.authorWebsite = authorWebsite;
        c.content = content;
        c.status = isGuest ? CommentStatus.PENDING : CommentStatus.APPROVED;
        c.userAgent = userAgent;
        c.ip = ip;
        c.setCreatedAt(LocalDateTime.now());
        return c;
    }

    public void approve() { this.status = CommentStatus.APPROVED; }
    public void reject() { this.status = CommentStatus.REJECTED; }

    // Getters provided by Lombok @Getter

    // Reconstruct
    public static Comment reconstruct(Long id, Long articleId, Long userId, Long parentId,
                                       Long replyToId, String authorName, String authorEmail,
                                       String authorWebsite, CommentContent content,
                                       CommentStatus status, String userAgent, String ip,
                                       LocalDateTime createdAt) {
        Comment c = new Comment();
        c.articleId = articleId;
        c.userId = userId;
        c.parentId = parentId;
        c.replyToId = replyToId;
        c.authorName = authorName;
        c.authorEmail = authorEmail;
        c.authorWebsite = authorWebsite;
        c.content = content;
        c.status = status;
        c.userAgent = userAgent;
        c.ip = ip;
        setBaseId(c, id);
        c.setCreatedAt(createdAt);
        return c;
    }

    private static void setBaseId(Comment c, Long id) {
        try {
            java.lang.reflect.Field f = BaseEntity.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(c, id);
        } catch (Exception ignored) {}
    }
}
