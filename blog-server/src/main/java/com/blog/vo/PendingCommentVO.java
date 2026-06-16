package com.blog.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 待审核评论 VO — 审核队列展示用（含文章标题、IP、UA 等管理信息）
 */
@Data
@NoArgsConstructor
public class PendingCommentVO {

    private Long id;
    private Long articleId;
    private String articleTitle;
    private String authorName;
    private String authorEmail;
    private String authorWebsite;
    private String content;
    private String status;
    private String ip;
    private String userAgent;
    private LocalDateTime createdAt;
}