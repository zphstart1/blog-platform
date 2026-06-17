package com.blog.article.domain;

import java.util.Optional;

/**
 * ArticleVersion 仓储接口
 */
public interface ArticleVersionRepository {

    ArticleVersion save(ArticleVersion version);

    /** 查询文章的最大版本号 */
    Optional<Integer> findMaxVersionNo(ArticleId articleId);

    void deleteByArticleId(ArticleId articleId);
}
