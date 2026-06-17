package com.blog.article.application.command;

import lombok.Data;

import java.util.List;

/**
 * 创建文章命令
 */
@Data
public class CreateArticleCommand {
    private String title;
    private String slug;
    private String content;
    private String summary;
    private String coverImage;
    private Long categoryId;
    private List<Long> tagIds;
    private String status; // DRAFT / PUBLISHED
    private Integer isTop;
}
