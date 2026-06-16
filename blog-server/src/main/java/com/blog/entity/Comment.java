package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 评论实体 — 对应 comment 表
 */
@Data
@Accessors(chain = true)
@TableName("comment")
public class Comment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long articleId;

    private Long userId;

    private Long parentId;

    private Long replyToId;

    private String authorName;

    private String authorEmail;

    private String authorWebsite;

    private String content;

    private String status;

    private String userAgent;

    private String ip;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}