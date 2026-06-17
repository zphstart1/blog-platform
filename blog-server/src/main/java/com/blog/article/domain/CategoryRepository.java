package com.blog.article.domain;

import java.util.List;
import java.util.Optional;

/**
 * Category 仓储接口
 */
public interface CategoryRepository {

    Category save(Category category);

    Category update(Category category);

    Optional<Category> findById(CategoryId id);

    Optional<Category> findBySlug(Slug slug);

    List<Category> findAll();

    boolean existsByName(String name, CategoryId excludeId);

    boolean existsBySlug(Slug slug, CategoryId excludeId);

    /** 是否有关联的文章 */
    boolean hasArticles(CategoryId id);

    /** 统计分类下的文章数量 */
    int countArticles(CategoryId id);

    void delete(CategoryId id);
}
