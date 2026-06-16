package com.blog.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文章状态枚举
 */
@Getter
@AllArgsConstructor
public enum ArticleStatus {

    DRAFT("DRAFT", "草稿"),
    PUBLISHED("PUBLISHED", "已发布");

    private final String code;
    private final String desc;
}