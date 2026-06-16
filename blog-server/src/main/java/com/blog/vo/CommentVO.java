package com.blog.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论 VO — 评论列表展示用
 */
@Data
@NoArgsConstructor
public class CommentVO {

    private Long id;
    private Long articleId;
    private Long parentId;
    private Long replyToId;
    private String authorName;
    private String authorWebsite;
    private String content;
    private String status;
    private LocalDateTime createdAt;
    /** 子评论（嵌套回复，最多2层） */
    private List<CommentVO> children;
}