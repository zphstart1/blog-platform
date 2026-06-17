package com.blog.article.domain;

import java.util.List;
import java.util.Optional;

/**
 * Tag 仓储接口
 */
public interface TagRepository {

    Tag save(Tag tag);

    Tag update(Tag tag);

    Optional<Tag> findById(TagId id);

    Optional<Tag> findBySlug(Slug slug);

    List<Tag> findAll();

    List<Tag> findByIds(List<TagId> ids);

    boolean existsByName(String name, TagId excludeId);

    boolean existsBySlug(Slug slug, TagId excludeId);

    void delete(TagId id);

    /** 保存文章-标签关联 */
    void saveArticleTags(ArticleId articleId, List<TagId> tagIds);

    /** 删除文章的所有标签关联 */
    void deleteArticleTags(ArticleId articleId);

    /** 查询文章关联的标签ID列表 */
    List<TagId> findTagIdsByArticleId(ArticleId articleId);

    /** 统计标签下的文章数量 */
    int countArticlesByTagId(TagId id);
}
