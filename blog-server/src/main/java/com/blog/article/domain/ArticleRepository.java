package com.blog.article.domain;

import java.util.List;
import java.util.Optional;

/**
 * Article 仓储接口 — Domain 层只定义接口，Infrastructure 层实现
 */
public interface ArticleRepository {

    /** 保存新文章 */
    Article save(Article article);

    /** 更新文章 */
    Article update(Article article);

    /** 按 ID 查找 */
    Optional<Article> findById(ArticleId id);

    /** 按 slug 查找 */
    Optional<Article> findBySlug(Slug slug);

    /** 判断 slug 是否已存在（排除指定文章） */
    boolean existsBySlug(Slug slug, ArticleId excludeId);

    /** 删除文章 */
    void delete(ArticleId id);

    /** 查询下一篇文章（已发布的） */
    Optional<Article> findPrevPublished(ArticleId currentId);

    /** 查询上一篇文章（已发布的） */
    Optional<Article> findNextPublished(ArticleId currentId);

    /** 批量更新阅读量（如 Redis 每10次回写） */
    void incrementViewCountBulk(ArticleId id, int delta);

    /** 查找用户的所有草稿 */
    List<Article> findDraftsByAuthor(Long authorId, int page, int size);

    /** 统计用户草稿总数 */
    long countDraftsByAuthor(Long authorId);
}
