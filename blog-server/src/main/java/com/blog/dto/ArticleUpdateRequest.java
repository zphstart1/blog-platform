package com.blog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 更新文章请求 DTO — 所有字段可选（只传需要更新的字段）
 */
@Data
@NoArgsConstructor
public class ArticleUpdateRequest {

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