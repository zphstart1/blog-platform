package com.blog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 创建文章请求 DTO
 */
@Data
@NoArgsConstructor
public class ArticleCreateRequest {

    @NotBlank(message = "文章标题不能为空")
    @Size(max = 200, message = "文章标题最长200字符")
    private String title;

    private String slug;

    @NotBlank(message = "文章内容不能为空")
    private String content;

    private String summary;

    private String coverImage;

    private Long categoryId;

    private List<Long> tagIds;

    /** DRAFT(默认) / PUBLISHED */
    private String status;

    private Integer isTop;
}