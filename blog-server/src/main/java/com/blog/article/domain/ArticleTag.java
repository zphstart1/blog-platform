package com.blog.article.domain;

/**
 * 文章-标签关联（Article 聚合内部实体）
 */
public class ArticleTag {

    private ArticleId articleId;
    private TagId tagId;

    protected ArticleTag() {}

    public static ArticleTag associate(ArticleId articleId, TagId tagId) {
        ArticleTag at = new ArticleTag();
        at.articleId = articleId;
        at.tagId = tagId;
        return at;
    }

    public ArticleId getArticleId() { return articleId; }
    public TagId getTagId() { return tagId; }
}
