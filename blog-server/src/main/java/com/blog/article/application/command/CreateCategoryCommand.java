package com.blog.article.application.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 创建分类命令
 */
@Data
public class CreateCategoryCommand {
    @NotBlank(message = "分类名称不能为空")
    private String name;
    private String slug;
    private String description;
    private Long parentId;
    private Integer sortOrder;
}
