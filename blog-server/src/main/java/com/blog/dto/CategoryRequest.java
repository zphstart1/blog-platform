package com.blog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

/**
 * 分类创建/更新请求 DTO
 */
@Data
@NoArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private String slug;

    private String description;

    private Long parentId;

    private Integer sortOrder;
}