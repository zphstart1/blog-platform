package com.blog.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章详情 VO — 文章详情页完整数据
 */
@Data
@NoArgsConstructor
public class ArticleDetailVO {

    private Long id;
    private String title;
    private String slug;
    private String content;
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
    /** 分类ID — 管理端编辑回填用 */
    private Long categoryId;
    /** 标签ID列表 — 管理端编辑回填用 */
    private List<Long> tagIds;
    /** 上一篇文章 */
    private NavArticle prevArticle;
    /** 下一篇文章 */
    private NavArticle nextArticle;
}