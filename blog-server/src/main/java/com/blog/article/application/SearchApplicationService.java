package com.blog.article.application;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.article.infrastructure.ArticleMapper;
import com.blog.shared.Result;
import com.blog.dto.SearchRequest;
import com.blog.vo.ArticleVO;
import com.blog.vo.SearchResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 搜索应用服务 — 基于 MySQL FULLTEXT 索引
 */
@Slf4j
@Service
public class SearchApplicationService {

    private final ArticleMapper articleMapper;

    public SearchApplicationService(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    /** 全文搜索 */
    public Result<SearchResultVO> search(SearchRequest request) {
        Page<ArticleVO> page = new Page<>(request.getPage(), request.getSize());
        List<ArticleVO> rawList = articleMapper.selectSearchPage(page, request.getKeyword());
        List<ArticleVO> mergedList = ArticleReadService.mergeArticleVOs(rawList);

        SearchResultVO result = new SearchResultVO();
        result.setRecords(mergedList);
        result.setTotal(page.getTotal());
        result.setPage(request.getPage());
        result.setSize(request.getSize());
        result.setPages((int) Math.ceil((double) page.getTotal() / request.getSize()));
        result.setKeyword(request.getKeyword());

        return Result.ok(result);
    }

    /** 搜索热词（暂返回空，后续可接入 Redis 搜索日志） */
    public Result<Set<String>> hotKeywords(int topN) {
        return Result.ok(Collections.emptySet());
    }
}
