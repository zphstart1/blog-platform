package com.blog.article.domain;

import com.blog.shared.BaseEntity;
import com.blog.shared.DomainException;
import com.blog.utils.MarkdownUtil;
import com.blog.utils.SlugUtil;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 文章聚合根 — article 限界上下文的核心
 *
 * <p>业务不变量：
 * <ul>
 *   <li>发布时内容不能为空</li>
 *   <li>slug 全局唯一（由 Application 层配合 Repository 校验）</li>
 *   <li>状态变更仅允许 DRAFT ↔ PUBLISHED</li>
 *   <li>DRAFT → PUBLISHED 时自动设置发布时间</li>
 * </ul>
 */
public class Article extends BaseEntity {

    private Title title;
    private Slug slug;
    private Content content;
    private String summary;
    private String coverImage;
    private CategoryId categoryId;
    private Long authorId;
    private ArticleStatus status;
    private boolean isTop;
    private ViewCount viewCount;
    private LocalDateTime publishedAt;
    private List<ArticleTag> tags;

    // MyBatis 需要无参构造
    protected Article() {}

    // ==================== 工厂方法 ====================

    /**
     * 创建新文章（草稿）
     */
    public static Article createDraft(Title title, Content content, Long authorId) {
        String generatedSlug = SlugUtil.generateSlug(title.value());
        String summary = MarkdownUtil.extractSummary(content.value(), 200);

        Article article = new Article();
        article.title = title;
        article.slug = Slug.of(generatedSlug);
        article.content = content;
        article.summary = summary;
        article.coverImage = null;
        article.categoryId = null;
        article.authorId = authorId;
        article.status = ArticleStatus.DRAFT;
        article.isTop = false;
        article.viewCount = ViewCount.zero();
        article.publishedAt = null;
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        return article;
    }

    /**
     * 创建文章（含 slug、分类、摘要等完整信息）
     */
    public static Article create(Title title, Slug slug, Content content,
                                  String summary, String coverImage,
                                  CategoryId categoryId, Long authorId,
                                  ArticleStatus status, boolean isTop) {
        if (status == ArticleStatus.PUBLISHED && content.isEmpty()) {
            throw new DomainException("发布文章时，内容不能为空");
        }

        Article article = new Article();
        article.title = title;
        article.slug = slug;
        article.content = content;
        article.summary = StringUtils.hasText(summary) ? summary : MarkdownUtil.extractSummary(content.value(), 200);
        article.coverImage = coverImage;
        article.categoryId = categoryId;
        article.authorId = authorId;
        article.status = status;
        article.isTop = isTop;
        article.viewCount = ViewCount.zero();
        article.publishedAt = status == ArticleStatus.PUBLISHED ? LocalDateTime.now() : null;
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        return article;
    }

    // ==================== 领域行为 ====================

    /**
     * 发布文章 — DRAFT → PUBLISHED
     */
    public void publish() {
        if (status == ArticleStatus.PUBLISHED) {
            return; // 幂等
        }
        if (content.isEmpty()) {
            throw new DomainException("发布文章时，内容不能为空");
        }
        this.status = ArticleStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
        this.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 撤回文章 — PUBLISHED → DRAFT
     */
    public void unpublish() {
        if (status == ArticleStatus.DRAFT) {
            return; // 幂等
        }
        this.status = ArticleStatus.DRAFT;
        this.publishedAt = null;
        this.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 更新标题
     */
    public void changeTitle(Title newTitle) {
        this.title = newTitle;
        this.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 更新 Slug（需外部校验唯一性）
     */
    public void changeSlug(Slug newSlug) {
        this.slug = newSlug;
        this.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 更新内容 — 自动提取摘要
     */
    public void updateContent(Content newContent, String explicitSummary) {
        this.content = newContent;
        this.summary = StringUtils.hasText(explicitSummary)
                ? explicitSummary
                : MarkdownUtil.extractSummary(newContent.value(), 200);
        this.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 更新摘要
     */
    public void changeSummary(String summary) {
        this.summary = summary;
        this.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 更新封面图
     */
    public void changeCoverImage(String coverImage) {
        this.coverImage = coverImage;
        this.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 更换分类
     */
    public void changeCategory(CategoryId categoryId) {
        this.categoryId = categoryId;
        this.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 切换置顶状态
     */
    public void toggleTop() {
        this.isTop = !this.isTop;
        this.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 设置置顶
     */
    public void setTop(boolean top) {
        this.isTop = top;
        this.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 增加阅读量
     */
    public void incrementViewCount() {
        this.viewCount = this.viewCount.increment();
    }

    /**
     * 批量增加阅读量
     */
    public void incrementViewCount(int delta) {
        this.viewCount = this.viewCount.increment(delta);
    }

    // ==================== Getters（只读） ====================

    public Title getTitle() { return title; }
    public Slug getSlug() { return slug; }
    public Content getContent() { return content; }
    public String getSummary() { return summary; }
    public String getCoverImage() { return coverImage; }
    public CategoryId getCategoryId() { return categoryId; }
    public Long getAuthorId() { return authorId; }
    public ArticleStatus getStatus() { return status; }
    public boolean isTop() { return isTop; }
    public ViewCount getViewCount() { return viewCount; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public List<ArticleTag> getTags() { return tags != null ? Collections.unmodifiableList(tags) : Collections.emptyList(); }

    /**
     * 从持久化重建聚合（仅供 Infrastructure 层 Converter 调用）
     * 不做业务校验，直接赋值重建
     */
    public static Article reconstruct(Long id, Title title, Slug slug, Content content,
                               String summary, String coverImage,
                               CategoryId categoryId, Long authorId,
                               ArticleStatus status, boolean isTop,
                               ViewCount viewCount, LocalDateTime publishedAt,
                               LocalDateTime createdAt, LocalDateTime updatedAt) {
        Article article = new Article();
        article.title = title;
        article.slug = slug;
        article.content = content;
        article.summary = summary;
        article.coverImage = coverImage;
        article.categoryId = categoryId;
        article.authorId = authorId;
        article.status = status;
        article.isTop = isTop;
        article.viewCount = viewCount;
        article.publishedAt = publishedAt;
        // BaseEntity 字段
        setField(article, "id", id);
        article.setCreatedAt(createdAt);
        article.setUpdatedAt(updatedAt);
        return article;
    }

    // 反射设置 BaseEntity 私有字段（仅 Infrastructure 层使用）
    private static void setField(Article target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = com.blog.shared.BaseEntity.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }
}
