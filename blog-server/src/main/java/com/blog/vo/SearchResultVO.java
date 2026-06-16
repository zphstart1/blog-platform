package com.blog.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 搜索结果 VO — 含关键词和分页信息
 */
@Data
@NoArgsConstructor
public class SearchResultVO {

    private List<ArticleVO> records;
    private long total;
    private int page;
    private int size;
    private int pages;
    private String keyword;
}