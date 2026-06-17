package com.blog.article.interfaces;

import com.blog.article.application.SearchApplicationService;
import com.blog.shared.Result;
import com.blog.dto.SearchRequest;
import com.blog.vo.SearchResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

/**
 * 搜索接口 — DDD interfaces 层（article Context 读侧）
 */
@Slf4j
@Api(tags = "搜索接口")
@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchApplicationService searchApplicationService;

    @ApiOperation("全文搜索")
    @GetMapping("/search")
    public Result<SearchResultVO> search(@Valid SearchRequest request) {
        return searchApplicationService.search(request);
    }

    @ApiOperation("搜索热词")
    @GetMapping("/search/hot")
    public Result<Set<String>> hotKeywords(@RequestParam(defaultValue = "20") int topN) {
        return searchApplicationService.hotKeywords(topN);
    }
}
