package com.blog;

import com.blog.utils.MarkdownUtil;
import com.blog.utils.SlugUtil;
import com.blog.utils.XssUtil;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 工具类单元测试 — Markdown渲染/Slug生成/XSS过滤
 */
class UtilsTest {

    @Test
    @DisplayName("Markdown渲染-标题")
    void markdownRender_heading() {
        String html = MarkdownUtil.renderToHtml("# Hello World");
        assertTrue(html.contains("<h1>"));
        assertTrue(html.contains("Hello World"));
    }

    @Test
    @DisplayName("Markdown渲染-代码块")
    void markdownRender_codeBlock() {
        String md = "```java\nSystem.out.println(\"hello\");\n```";
        String html = MarkdownUtil.renderToHtml(md);
        assertTrue(html.contains("<code>"));
    }

    @Test
    @DisplayName("Markdown渲染-空内容")
    void markdownRender_empty() {
        assertEquals("", MarkdownUtil.renderToHtml(null));
        assertEquals("", MarkdownUtil.renderToHtml(""));
    }

    @Test
    @DisplayName("Markdown渲染-GFM表格")
    void markdownRender_table() {
        String md = "| Name | Age |\n| --- | --- |\n| Tom | 25 |";
        String html = MarkdownUtil.renderToHtml(md);
        assertTrue(html.contains("<table>"));
    }

    @Test
    @DisplayName("摘要提取-正常")
    void extractSummary_normal() {
        String summary = MarkdownUtil.extractSummary("# Hello\nThis is content with **bold** and `code`", 50);
        assertNotNull(summary);
        assertTrue(summary.length() <= 53); // 50 + "..."
    }

    @Test
    @DisplayName("摘要提取-空内容")
    void extractSummary_empty() {
        assertEquals("", MarkdownUtil.extractSummary(null, 50));
        assertEquals("", MarkdownUtil.extractSummary("", 50));
    }

    // ========== Slug生成 ==========

    @Test
    @DisplayName("Slug生成-英文标题")
    void generateSlug_english() {
        String slug = SlugUtil.generateSlug("Spring Boot Tutorial");
        assertEquals("spring-boot-tutorial", slug);
    }

    @Test
    @DisplayName("Slug生成-中文标题")
    void generateSlug_chinese() {
        String slug = SlugUtil.generateSlug("Spring Boot入门教程");
        assertNotNull(slug);
        assertTrue(slug.length() > 0);
        // 中文标题使用UUID，长度8
        assertEquals(8, slug.length());
    }

    @Test
    @DisplayName("Slug生成-空标题")
    void generateSlug_empty() {
        String slug = SlugUtil.generateSlug("");
        assertNotNull(slug);
        assertEquals(8, slug.length()); // UUID短格式
    }

    @Test
    @DisplayName("Slug生成-特殊字符")
    void generateSlug_specialChars() {
        String slug = SlugUtil.generateSlug("Hello!!! World??? --- Test");
        assertTrue(slug.contains("hello"));
        assertFalse(slug.contains("!"));
        assertFalse(slug.contains("?"));
    }

    // ========== XSS过滤 ==========

    @Test
    @DisplayName("XSS过滤-HTML实体转义")
    void escapeHtml_script() {
        String escaped = XssUtil.escapeHtml("<script>alert('xss')</script>");
        assertTrue(escaped.contains("&lt;"));
        assertTrue(escaped.contains("&gt;"));
        assertFalse(escaped.contains("<script>"));
    }

    @Test
    @DisplayName("XSS过滤-去除HTML标签")
    void stripHtmlTags() {
        String stripped = XssUtil.stripHtmlTags("<p>Hello <b>World</b></p>");
        assertEquals("Hello World", stripped);
    }

    @Test
    @DisplayName("XSS过滤-空输入")
    void escapeHtml_null() {
        assertEquals("", XssUtil.escapeHtml(null));
        assertEquals("", XssUtil.stripHtmlTags(null));
    }
}