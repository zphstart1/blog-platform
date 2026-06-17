package com.blog.article.domain.events;

import com.blog.article.domain.Article;
import com.blog.article.domain.ArticleId;
import com.blog.article.domain.ArticleStatus;

import java.time.LocalDateTime;

/**
 * 文章创建领域事件
 */
public class ArticleCreatedEvent {

    private final ArticleId articleId;
    private final String title;
    private final String slug;
    private final ArticleStatus status;
    private final Long authorId;
    private final LocalDateTime occurredAt;

    public ArticleCreatedEvent(Article article) {
        this.articleId = article.getId() != null ? ArticleId.of(article.getId()) : ArticleId.of(0L);
        this.title = article.getTitle().value();
        this.slug = article.getSlug().value();
        this.status = article.getStatus();
        this.authorId = article.getAuthorId();
        this.occurredAt = LocalDateTime.now();
    }

    public ArticleId getArticleId() { return articleId; }
    public String getTitle() { return title; }
    public String getSlug() { return slug; }
    public ArticleStatus getStatus() { return status; }
    public Long getAuthorId() { return authorId; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
}
