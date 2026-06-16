package com.blog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * 管理端文章列表分页请求 DTO
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdminArticlePageRequest extends PageRequest {

    private String status;

    private Long categoryId;
}