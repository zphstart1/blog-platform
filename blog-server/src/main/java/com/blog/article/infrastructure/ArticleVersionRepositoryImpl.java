package com.blog.article.infrastructure;

import com.blog.article.domain.ArticleId;
import com.blog.article.domain.ArticleVersion;
import com.blog.article.domain.ArticleVersionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ArticleVersionRepository 实现
 */
@Repository
public class ArticleVersionRepositoryImpl implements ArticleVersionRepository {

    private final ArticleVersionMapper articleVersionMapper;

    public ArticleVersionRepositoryImpl(ArticleVersionMapper articleVersionMapper) {
        this.articleVersionMapper = articleVersionMapper;
    }

    @Override
    public ArticleVersion save(ArticleVersion version) {
        ArticleVersionPO po = ArticleConverter.toPO(version);
        articleVersionMapper.insert(po);
        return version;
    }

    @Override
    public Optional<Integer> findMaxVersionNo(ArticleId articleId) {
        Integer maxVersion = articleVersionMapper.findMaxVersionNo(articleId.value());
        return Optional.ofNullable(maxVersion);
    }

    @Override
    public void deleteByArticleId(ArticleId articleId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ArticleVersionPO> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(ArticleVersionPO::getArticleId, articleId.value());
        articleVersionMapper.delete(wrapper);
    }
}
