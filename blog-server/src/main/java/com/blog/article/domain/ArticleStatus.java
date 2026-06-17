package com.blog.article.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文章状态枚举 — 领域层核心概念
 */
@Getter
@AllArgsConstructor
public enum ArticleStatus {

    DRAFT("DRAFT", "草稿"),
    PUBLISHED("PUBLISHED", "已发布");

    private final String code;
    private final String desc;

    public static ArticleStatus fromCode(String code) {
        for (ArticleStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return DRAFT;
    }

    public boolean isPublished() {
        return this == PUBLISHED;
    }

    public boolean isDraft() {
        return this == DRAFT;
    }
}
