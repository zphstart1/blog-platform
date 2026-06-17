package com.blog.article.application.command;

import lombok.Data;

import java.util.List;

/**
 * 更新文章命令 — 所有字段可选
 */
@Data
public class UpdateArticleCommand {
    private String title;
    private String slug;
    private String content;
    private String summary;
    private String coverImage;
    private Long categoryId;
    private List<Long> tagIds;
    private String status;
    private Integer isTop;
}
