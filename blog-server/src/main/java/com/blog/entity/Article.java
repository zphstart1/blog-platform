package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 文章实体 — 对应 article 表
 */
@Data
@Accessors(chain = true)
@TableName("article")
public class Article {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String slug;

    private String content;

    private String contentHtml;

    private String summary;

    private String coverImage;

    private Long categoryId;

    private Long authorId;

    private String status;

    private Integer isTop;

    private Integer viewCount;

    private LocalDateTime publishedAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}