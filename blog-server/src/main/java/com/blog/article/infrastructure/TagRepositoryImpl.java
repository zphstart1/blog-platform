package com.blog.article.infrastructure;

import com.blog.article.domain.*;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TagRepository 实现
 */
@Repository
public class TagRepositoryImpl implements TagRepository {

    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;

    public TagRepositoryImpl(TagMapper tagMapper, ArticleTagMapper articleTagMapper) {
        this.tagMapper = tagMapper;
        this.articleTagMapper = articleTagMapper;
    }

    @Override
    public Tag save(Tag tag) {
        TagPO po = ArticleConverter.toPO(tag);
        tagMapper.insert(po);
        return ArticleConverter.toDomain(po);
    }

    @Override
    public Tag update(Tag tag) {
        TagPO po = ArticleConverter.toPO(tag);
        tagMapper.updateById(po);
        return tag;
    }

    @Override
    public Optional<Tag> findById(TagId id) {
        return Optional.ofNullable(tagMapper.selectById(id.value()))
                .map(ArticleConverter::toDomain);
    }

    @Override
    public Optional<Tag> findBySlug(Slug slug) {
        return tagMapper.findBySlug(slug.value())
                .map(ArticleConverter::toDomain);
    }

    @Override
    public List<Tag> findAll() {
        return tagMapper.selectList(null).stream()
                .map(ArticleConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tag> findByIds(List<TagId> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        String joined = ids.stream().map(id -> id.value().toString())
                .collect(Collectors.joining(","));
        return tagMapper.findByIds(joined).stream()
                .map(ArticleConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByName(String name, TagId excludeId) {
        Long exclude = excludeId != null ? excludeId.value() : null;
        return tagMapper.countByNameExclude(name, exclude) > 0;
    }

    @Override
    public boolean existsBySlug(Slug slug, TagId excludeId) {
        Long exclude = excludeId != null ? excludeId.value() : null;
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<TagPO> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(TagPO::getSlug, slug.value());
        if (exclude != null) {
            wrapper.ne(TagPO::getId, exclude);
        }
        return tagMapper.selectCount(wrapper) > 0;
    }

    @Override
    public void delete(TagId id) {
        tagMapper.deleteById(id.value());
    }

    @Override
    public void saveArticleTags(ArticleId articleId, List<TagId> tagIds) {
        for (TagId tagId : tagIds) {
            articleTagMapper.insertIgnore(articleId.value(), tagId.value());
        }
    }

    @Override
    public void deleteArticleTags(ArticleId articleId) {
        articleTagMapper.deleteByArticleId(articleId.value());
    }

    @Override
    public List<TagId> findTagIdsByArticleId(ArticleId articleId) {
        List<Long> ids = articleTagMapper.findTagIdsByArticleId(articleId.value());
        return ArticleConverter.toTagIds(ids);
    }

    @Override
    public int countArticlesByTagId(TagId id) {
        return articleTagMapper.countByTagId(id.value());
    }
}
