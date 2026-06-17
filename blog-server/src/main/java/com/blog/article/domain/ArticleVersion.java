package com.blog.article.domain;

import com.blog.shared.BaseEntity;

import java.time.LocalDateTime;

/**
 * 文章版本快照实体（Article 聚合内部实体）
 */
public class ArticleVersion extends BaseEntity {

    private ArticleId articleId;
    private int versionNo;
    private String content;
    private Long editorId;

    protected ArticleVersion() {}

    public static ArticleVersion create(ArticleId articleId, int versionNo, String content, Long editorId) {
        ArticleVersion version = new ArticleVersion();
        version.articleId = articleId;
        version.versionNo = versionNo;
        version.content = content;
        version.editorId = editorId;
        version.setCreatedAt(LocalDateTime.now());
        return version;
    }

    // ==================== Getters ====================
    public ArticleId getArticleId() { return articleId; }
    public int getVersionNo() { return versionNo; }
    public String getContent() { return content; }
    public Long getEditorId() { return editorId; }
}
