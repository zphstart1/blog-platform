package com.blog.utils;

import lombok.extern.slf4j.Slf4j;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import java.util.List;

/**
 * Markdown → HTML 渲染工具类
 * 使用 commonmark-java 库，支持 GFM 表格扩展
 */
@Slf4j
public class MarkdownUtil {

    private static final List<Extension> extensions = List.of(TablesExtension.create());
    private static final Parser parser = Parser.builder().extensions(extensions).build();
    private static final HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).build();

    /**
     * 将 Markdown 文本渲染为 HTML
     */
    public static String renderToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }
        try {
            return renderer.render(parser.parse(markdown));
        } catch (Exception e) {
            log.warn("Markdown渲染失败: {}", e.getMessage());
            return markdown;
        }
    }

    /**
     * 从 Markdown 内容自动提取摘要（截取前200字符的纯文本）
     */
    public static String extractSummary(String markdown, int maxLength) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }
        // 去掉 Markdown 标记符号，提取纯文本
        String plain = markdown.replaceAll("[#*`\\[\\]\\(\\)!>~-]", "").trim();
        if (plain.length() <= maxLength) {
            return plain;
        }
        return plain.substring(0, maxLength) + "...";
    }
}