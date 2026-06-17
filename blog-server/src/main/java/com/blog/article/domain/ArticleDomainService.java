package com.blog.article.domain;

import com.blog.article.domain.events.ArticleCreatedEvent;
import com.blog.article.domain.events.ArticleUpdatedEvent;
import com.blog.shared.DomainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 文章领域服务 — 处理跨聚合的领域逻辑
 */
@Slf4j
@Service
public class ArticleDomainService {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final ArticleVersionRepository articleVersionRepository;

    public ArticleDomainService(ArticleRepository articleRepository,
                                 CategoryRepository categoryRepository,
                                 ArticleVersionRepository articleVersionRepository) {
        this.articleRepository = articleRepository;
        this.categoryRepository = categoryRepository;
        this.articleVersionRepository = articleVersionRepository;
    }

    /**
     * 校验 slug 唯一性
     */
    public void validateSlugUnique(Slug slug, ArticleId excludeId) {
        if (articleRepository.existsBySlug(slug, excludeId)) {
            throw new DomainException("slug 已存在: " + slug.value());
        }
    }

    /**
     * 发布文章前校验（内容非空等）
     */
    public void validateBeforePublish(Article article) {
        if (article.getContent().isEmpty()) {
            throw new DomainException("发布文章时，内容不能为空");
        }
    }

    /**
     * 校验分类是否存在
     */
    public void validateCategoryExists(CategoryId categoryId) {
        if (categoryId != null) {
            Optional<Category> category = categoryRepository.findById(categoryId);
            if (category.isEmpty()) {
                throw new DomainException("分类不存在: " + categoryId.value());
            }
        }
    }

    /**
     * 删除分类前校验（有关联文章时不可删）
     */
    public void validateCategoryCanBeDeleted(CategoryId categoryId) {
        if (categoryRepository.hasArticles(categoryId)) {
            throw new DomainException("该分类下有文章，无法删除");
        }
    }

    /**
     * 保存文章版本快照
     */
    public void saveVersionSnapshot(Article article, Long editorId) {
        try {
            Optional<Integer> maxVersionOpt = articleVersionRepository.findMaxVersionNo(ArticleId.of(article.getId()));
            int nextVersion = maxVersionOpt.map(v -> v + 1).orElse(1);

            ArticleVersion version = ArticleVersion.create(
                    ArticleId.of(article.getId()),
                    nextVersion,
                    article.getContent().value(),
                    editorId
            );
            articleVersionRepository.save(version);
            log.debug("文章版本快照已保存: articleId={}, versionNo={}", article.getId(), nextVersion);
        } catch (Exception e) {
            log.warn("保存文章版本快照失败(非致命): articleId={}, error={}", article.getId(), e.getMessage());
        }
    }

    /**
     * 领域事件: 文章创建后
     */
    public ArticleCreatedEvent onArticleCreated(Article article) {
        return new ArticleCreatedEvent(article);
    }

    /**
     * 领域事件: 文章更新后
     */
    public ArticleUpdatedEvent onArticleUpdated(Article article) {
        return new ArticleUpdatedEvent(article);
    }

    /**
     * 删除分类关联的标签关联
     */
    public void cleanArticleTagsOnDelete(ArticleId articleId, TagRepository tagRepository) {
        tagRepository.deleteArticleTags(articleId);
    }
}
