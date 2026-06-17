package com.blog.article.domain.events;

import com.blog.article.domain.Article;
import com.blog.article.domain.ArticleId;

import java.time.LocalDateTime;

/**
 * 文章更新领域事件
 */
public class ArticleUpdatedEvent {

    private final ArticleId articleId;
    private final String slug;
    private final Long authorId;
    private final LocalDateTime occurredAt;

    public ArticleUpdatedEvent(Article article) {
        this.articleId = ArticleId.of(article.getId());
        this.slug = article.getSlug().value();
        this.authorId = article.getAuthorId();
        this.occurredAt = LocalDateTime.now();
    }

    public ArticleId getArticleId() { return articleId; }
    public String getSlug() { return slug; }
    public Long getAuthorId() { return authorId; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
}
