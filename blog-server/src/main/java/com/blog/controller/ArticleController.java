package com.blog.controller;

import com.blog.common.Ret;
import com.blog.dto.*;
import com.blog.service.ArticleService;
import com.blog.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 公开文章接口 — 文章列表/详情，不需要鉴权
 */
@Slf4j
@Api(tags = "文章接口（公开）")
@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @ApiOperation("文章列表（公开）")
    @GetMapping
    public Ret<PageResult<ArticleVO>> list(ArticlePageRequest request) {
        return articleService.publicArticlePage(request);
    }

    @ApiOperation("文章详情（公开）")
    @GetMapping("/{slug}")
    public Ret<ArticleDetailVO> detail(@PathVariable String slug) {
        return articleService.articleDetail(slug);
    }
}