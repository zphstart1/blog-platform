package com.blog.controller;

import com.blog.common.Ret;
import com.blog.dto.SearchRequest;
import com.blog.service.SearchService;
import com.blog.vo.SearchResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

/**
 * 搜索接口 — 全文搜索 + 热词
 */
@Slf4j
@Api(tags = "搜索接口")
@RestController
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @ApiOperation("全文搜索")
    @GetMapping("/search")
    public Ret<SearchResultVO> search(@Valid SearchRequest request) {
        return searchService.search(request);
    }

    @ApiOperation("搜索热词")
    @GetMapping("/search/hot")
    public Ret<Set<String>> hotKeywords(@RequestParam(defaultValue = "20") int topN) {
        return searchService.hotKeywords(topN);
    }
}