package com.blog.utils;

/**
 * XSS 防护工具 — 评论内容 HTML 实体转义
 * 评论内容存储纯文本，展示时由前端做转义
 * 此工具用于对输入内容做兜底过滤，防止恶意 HTML 注入
 */
public class XssUtil {

    /**
     * HTML 实体转义 — 将评论内容中的特殊字符转为 HTML 实体
     */
    public static String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    /**
     * 去除所有 HTML 标签 — 评论内容只保留纯文本
     */
    public static String stripHtmlTags(String input) {
        if (input == null) {
            return "";
        }
        return input.replaceAll("<[^>]*>", "");
    }
}