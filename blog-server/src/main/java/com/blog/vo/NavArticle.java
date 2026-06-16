package com.blog.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 上下篇导航 VO — 文章详情页的 prev/next 简要信息
 */
@Data
@NoArgsConstructor
public class NavArticle {

    private Long id;
    private String title;
    private String slug;
}