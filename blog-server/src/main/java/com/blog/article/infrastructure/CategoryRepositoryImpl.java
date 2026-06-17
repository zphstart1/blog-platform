package com.blog.article.infrastructure;

import com.blog.article.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CategoryRepository 实现
 */
@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryMapper categoryMapper;
    private final ArticleMapper articleMapper;

    public CategoryRepositoryImpl(CategoryMapper categoryMapper, ArticleMapper articleMapper) {
        this.categoryMapper = categoryMapper;
        this.articleMapper = articleMapper;
    }

    @Override
    public Category save(Category category) {
        CategoryPO po = ArticleConverter.toPO(category);
        categoryMapper.insert(po);
        return ArticleConverter.toDomain(po);
    }

    @Override
    public Category update(Category category) {
        CategoryPO po = ArticleConverter.toPO(category);
        categoryMapper.updateById(po);
        return category;
    }

    @Override
    public Optional<Category> findById(CategoryId id) {
        return Optional.ofNullable(categoryMapper.selectById(id.value()))
                .map(ArticleConverter::toDomain);
    }

    @Override
    public Optional<Category> findBySlug(Slug slug) {
        return categoryMapper.findBySlug(slug.value())
                .map(ArticleConverter::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return categoryMapper.findAllOrdered().stream()
                .map(ArticleConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByName(String name, CategoryId excludeId) {
        Long exclude = excludeId != null ? excludeId.value() : null;
        return categoryMapper.countByNameExclude(name, exclude) > 0;
    }

    @Override
    public boolean existsBySlug(Slug slug, CategoryId excludeId) {
        Long exclude = excludeId != null ? excludeId.value() : null;
        // Use LambdaWrapper for slug check
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CategoryPO> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(CategoryPO::getSlug, slug.value());
        if (exclude != null) {
            wrapper.ne(CategoryPO::getId, exclude);
        }
        return categoryMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean hasArticles(CategoryId id) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ArticlePO> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(ArticlePO::getCategoryId, id.value());
        return articleMapper.selectCount(wrapper) > 0;
    }

    @Override
    public int countArticles(CategoryId id) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ArticlePO> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(ArticlePO::getCategoryId, id.value());
        Long count = articleMapper.selectCount(wrapper);
        return count != null ? count.intValue() : 0;
    }

    @Override
    public void delete(CategoryId id) {
        categoryMapper.deleteById(id.value());
    }
}
