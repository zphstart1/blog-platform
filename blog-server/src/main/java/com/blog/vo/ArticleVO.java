package com.blog.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章列表项 VO — 列表/搜索结果中的文章数据
 */
@Data
@NoArgsConstructor
public class ArticleVO {

    private Long id;
    private String title;
    private String slug;
    /** 文章 Markdown 原文 — MyBatis 映射 article.content */
    private String content;
    /** 文章渲染后的 HTML — MyBatis 映射 article.content_html */
    private String contentHtml;
    private String summary;
    private String coverImage;
    private CategoryVO category;
    private List<TagVO> tags;
    private AuthorVO author;
    private Integer viewCount;
    private Boolean isTop;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    /** 状态字段仅管理端可见 */
    private String status;
    /** 搜索结果相关度分数 */
    private Double relevanceScore;
}