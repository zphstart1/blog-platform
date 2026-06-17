package com.blog.article.infrastructure;

import com.blog.article.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ArticleRepository 实现 — 通过 MyBatis-Plus 操作数据库
 */
@org.springframework.stereotype.Repository
public class ArticleRepositoryImpl implements ArticleRepository {

    private final ArticleMapper articleMapper;

    public ArticleRepositoryImpl(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @Override
    public Article save(Article article) {
        ArticlePO po = ArticleConverter.toPO(article);
        articleMapper.insert(po);
        return ArticleConverter.toDomain(po);
    }

    @Override
    public Article update(Article article) {
        ArticlePO po = ArticleConverter.toPO(article);
        articleMapper.updateById(po);
        return article;
    }

    @Override
    public Optional<Article> findById(ArticleId id) {
        ArticlePO po = articleMapper.selectById(id.value());
        return Optional.ofNullable(ArticleConverter.toDomain(po));
    }

    @Override
    public Optional<Article> findBySlug(Slug slug) {
        return articleMapper.findBySlug(slug.value())
                .map(ArticleConverter::toDomain);
    }

    @Override
    public boolean existsBySlug(Slug slug, ArticleId excludeId) {
        Long exclude = excludeId != null ? excludeId.value() : null;
        return articleMapper.countBySlugExclude(slug.value(), exclude) > 0;
    }

    @Override
    public void delete(ArticleId id) {
        articleMapper.deleteById(id.value());
    }

    @Override
    public Optional<Article> findPrevPublished(ArticleId currentId) {
        return articleMapper.findPrevPublished(currentId.value())
                .map(ArticleConverter::toDomain);
    }

    @Override
    public Optional<Article> findNextPublished(ArticleId currentId) {
        return articleMapper.findNextPublished(currentId.value())
                .map(ArticleConverter::toDomain);
    }

    @Override
    public void incrementViewCountBulk(ArticleId id, int delta) {
        articleMapper.incrementViewCount(id.value(), delta);
    }

    @Override
    public List<Article> findDraftsByAuthor(Long authorId, int page, int size) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ArticlePO> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(ArticlePO::getAuthorId, authorId);
        wrapper.eq(ArticlePO::getStatus, ArticleStatus.DRAFT.getCode());
        wrapper.orderByDesc(ArticlePO::getUpdatedAt);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ArticlePO> p =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ArticlePO> result =
                articleMapper.selectPage(p, wrapper);

        return result.getRecords().stream()
                .map(ArticleConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countDraftsByAuthor(Long authorId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ArticlePO> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(ArticlePO::getAuthorId, authorId);
        wrapper.eq(ArticlePO::getStatus, ArticleStatus.DRAFT.getCode());
        return articleMapper.selectCount(wrapper);
    }
}
