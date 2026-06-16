package com.blog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * 文章列表分页请求 DTO
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ArticlePageRequest extends PageRequest {

    private Long categoryId;

    private Long tagId;

    private String keyword;

    private String sort;

    private String order;
}