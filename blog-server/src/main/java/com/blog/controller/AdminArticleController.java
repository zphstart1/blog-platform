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
 * 管理端文章接口 — CRUD/草稿管理，需要鉴权
 */
@Slf4j
@Api(tags = "文章管理接口（管理端）")
@RestController
@RequestMapping("/admin/articles")
public class AdminArticleController {

    private final ArticleService articleService;

    public AdminArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @ApiOperation("管理端文章列表")
    @GetMapping
    public Ret<PageResult<ArticleVO>> list(AdminArticlePageRequest request) {
        return articleService.adminArticlePage(request);
    }

    @ApiOperation("管理端获取单条文章 — 编辑回填用")
    @GetMapping("/{id}")
    public Ret<ArticleDetailVO> getById(@PathVariable Long id) {
        return articleService.adminArticleById(id);
    }

    @ApiOperation("创建文章")
    @PostMapping
    public Ret<ArticleVO> create(@Valid @RequestBody ArticleCreateRequest request) {
        return articleService.createArticle(request);
    }

    @ApiOperation("更新文章")
    @PutMapping("/{id}")
    public Ret<ArticleVO> update(@PathVariable Long id, @RequestBody ArticleUpdateRequest request) {
        return articleService.updateArticle(id, request);
    }

    @ApiOperation("删除文章")
    @DeleteMapping("/{id}")
    public Ret<Void> delete(@PathVariable Long id) {
        return articleService.deleteArticle(id);
    }

    @ApiOperation("草稿列表")
    @GetMapping("/drafts")
    public Ret<PageResult<ArticleVO>> drafts(PageRequest request) {
        return articleService.draftList(request);
    }
}