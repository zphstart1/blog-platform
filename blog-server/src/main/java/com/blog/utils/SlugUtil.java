package com.blog.utils;

import cn.hutool.core.util.StrUtil;
import java.util.UUID;

/**
 * Slug 生成工具 — 从标题自动生成URL友好的slug
 * 中文标题使用 UUID 确保唯一性，英文标题做 kebab-case 转换
 */
public class SlugUtil {

    /**
     * 从标题生成 slug：英文标题转 kebab-case，中文标题使用短UUID
     */
    public static String generateSlug(String title) {
        if (StrUtil.isBlank(title)) {
            return UUID.randomUUID().toString().substring(0, 8);
        }
        // 如果标题包含中文字符，使用短UUID
        if (title.matches(".+[\\u4e00-\\u9fa5].+")) {
            return UUID.randomUUID().toString().substring(0, 8);
        }
        // 英文标题转 kebab-case
        String slug = title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
        // 限制slug长度
        if (slug.length() > 100) {
            slug = slug.substring(0, 100);
        }
        return slug;
    }
}