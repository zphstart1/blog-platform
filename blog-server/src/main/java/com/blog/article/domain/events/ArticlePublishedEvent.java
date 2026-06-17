package com.blog.article.domain.events;

import com.blog.article.domain.Article;
import com.blog.article.domain.ArticleId;

import java.time.LocalDateTime;

/**
 * 文章发布领域事件
 */
public class ArticlePublishedEvent {

    private final ArticleId articleId;
    private final String slug;
    private final Long authorId;
    private final LocalDateTime publishedAt;
    private final LocalDateTime occurredAt;

    public ArticlePublishedEvent(Article article) {
        this.articleId = ArticleId.of(article.getId());
        this.slug = article.getSlug().value();
        this.authorId = article.getAuthorId();
        this.publishedAt = article.getPublishedAt();
        this.occurredAt = LocalDateTime.now();
    }

    public ArticleId getArticleId() { return articleId; }
    public String getSlug() { return slug; }
    public Long getAuthorId() { return authorId; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
}
