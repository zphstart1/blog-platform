package com.blog.article.infrastructure;

import com.blog.article.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Article Context 的 PO ↔ Domain 转换器
 */
public final class ArticleConverter {

    private ArticleConverter() {}

    // ==================== Article ====================

    public static ArticlePO toPO(Article domain) {
        ArticlePO po = new ArticlePO();
        if (domain.getId() != null) po.setId(domain.getId());
        po.setTitle(domain.getTitle().value());
        po.setSlug(domain.getSlug().value());
        po.setContent(domain.getContent().value());
        po.setSummary(domain.getSummary());
        po.setCoverImage(domain.getCoverImage());
        po.setCategoryId(domain.getCategoryId() != null ? domain.getCategoryId().value() : null);
        po.setAuthorId(domain.getAuthorId());
        po.setStatus(domain.getStatus().getCode());
        po.setIsTop(domain.isTop() ? 1 : 0);
        po.setViewCount(domain.getViewCount().value());
        po.setPublishedAt(domain.getPublishedAt());
        po.setCreatedAt(domain.getCreatedAt());
        po.setUpdatedAt(domain.getUpdatedAt());
        return po;
    }

    public static Article toDomain(ArticlePO po) {
        if (po == null) return null;
        return Article.reconstruct(
                po.getId(),
                Title.of(po.getTitle()),
                Slug.of(po.getSlug()),
                Content.of(po.getContent()),
                po.getSummary(),
                po.getCoverImage(),
                po.getCategoryId() != null ? CategoryId.of(po.getCategoryId()) : null,
                po.getAuthorId(),
                ArticleStatus.fromCode(po.getStatus()),
                po.getIsTop() != null && po.getIsTop() == 1,
                ViewCount.of(po.getViewCount() != null ? po.getViewCount() : 0),
                po.getPublishedAt(),
                po.getCreatedAt(),
                po.getUpdatedAt()
        );
    }

    // ==================== Category ====================

    public static CategoryPO toPO(Category domain) {
        CategoryPO po = new CategoryPO();
        if (domain.getId() != null) po.setId(domain.getId());
        po.setName(domain.getName());
        po.setSlug(domain.getSlug().value());
        po.setDescription(domain.getDescription());
        po.setParentId(domain.getParentId() != null ? domain.getParentId().value() : null);
        po.setSortOrder(domain.getSortOrder());
        po.setCreatedAt(domain.getCreatedAt());
        return po;
    }

    public static Category toDomain(CategoryPO po) {
        if (po == null) return null;
        Category category = Category.create(
                po.getName(),
                Slug.of(po.getSlug()),
                po.getDescription(),
                po.getParentId() != null ? CategoryId.of(po.getParentId()) : null,
                po.getSortOrder() != null ? po.getSortOrder() : 0
        );
        setBaseFields(category, po.getId(), po.getCreatedAt());
        return category;
    }

    // ==================== Tag ====================

    public static TagPO toPO(Tag domain) {
        TagPO po = new TagPO();
        if (domain.getId() != null) po.setId(domain.getId());
        po.setName(domain.getName());
        po.setSlug(domain.getSlug().value());
        po.setCreatedAt(domain.getCreatedAt());
        return po;
    }

    public static Tag toDomain(TagPO po) {
        if (po == null) return null;
        Tag tag = Tag.create(po.getName(), Slug.of(po.getSlug()));
        setBaseFields(tag, po.getId(), po.getCreatedAt());
        return tag;
    }

    // ==================== Link ====================

    public static LinkPO toPO(Link domain) {
        LinkPO po = new LinkPO();
        if (domain.getId() != null) po.setId(domain.getId());
        po.setName(domain.getName());
        po.setUrl(domain.getUrl());
        po.setLogo(domain.getLogo());
        po.setDescription(domain.getDescription());
        po.setStatus(domain.getStatus());
        po.setSortOrder(domain.getSortOrder());
        po.setCreatedAt(domain.getCreatedAt());
        return po;
    }

    public static Link toDomain(LinkPO po) {
        if (po == null) return null;
        Link link = Link.create(
                po.getName(), po.getUrl(), po.getLogo(),
                po.getDescription(),
                po.getSortOrder() != null ? po.getSortOrder() : 0
        );
        setBaseFields(link, po.getId(), po.getCreatedAt());
        return link;
    }

    // ==================== ArticleVersion ====================

    public static ArticleVersionPO toPO(ArticleVersion domain) {
        ArticleVersionPO po = new ArticleVersionPO();
        if (domain.getId() != null) po.setId(domain.getId());
        po.setArticleId(domain.getArticleId().value());
        po.setVersionNo(domain.getVersionNo());
        po.setContent(domain.getContent());
        po.setEditorId(domain.getEditorId());
        po.setCreatedAt(domain.getCreatedAt());
        return po;
    }

    public static ArticleVersion toDomain(ArticleVersionPO po) {
        if (po == null) return null;
        ArticleVersion version = ArticleVersion.create(
                ArticleId.of(po.getArticleId()),
                po.getVersionNo(),
                po.getContent(),
                po.getEditorId()
        );
        setBaseFields(version, po.getId(), po.getCreatedAt());
        return version;
    }

    // ==================== Tag IDs ====================

    public static List<TagId> toTagIds(List<Long> ids) {
        if (ids == null) return Collections.emptyList();
        return ids.stream().map(TagId::of).collect(Collectors.toList());
    }

    // ==================== 私有工具 ====================

    private static void setBaseFields(Object target, Long id, java.time.LocalDateTime createdAt) {
        try {
            if (target instanceof com.blog.shared.BaseEntity) {
                com.blog.shared.BaseEntity base = (com.blog.shared.BaseEntity) target;
                java.lang.reflect.Field idField = com.blog.shared.BaseEntity.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(base, id);
                base.setCreatedAt(createdAt);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to set base fields", e);
        }
    }
}
