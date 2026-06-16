package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 文章版本历史实体 — 对应 article_version 表 (P2)
 */
@Data
@Accessors(chain = true)
@TableName("article_version")
public class ArticleVersion {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long articleId;

    private Integer versionNo;

    private String content;

    private String contentHtml;

    private String changeNote;

    private Long editorId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}