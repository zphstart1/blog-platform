package com.blog.article.interfaces;

import com.blog.article.application.ArticleReadService;
import com.blog.shared.Result;
import com.blog.dto.ArticlePageRequest;
import com.blog.dto.PageResult;
import com.blog.vo.ArticleDetailVO;
import com.blog.vo.ArticleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 公开文章接口 — DDD interfaces 层（article Context 读侧）
 */
@Slf4j
@Api(tags = "文章接口（公开）")
@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleReadService articleReadService;

    @ApiOperation("文章列表（公开）")
    @GetMapping
    public Result<PageResult<ArticleVO>> list(ArticlePageRequest request) {
        return articleReadService.publicArticlePage(request);
    }

    @ApiOperation("文章详情（公开）")
    @GetMapping("/{slug}")
    public Result<ArticleDetailVO> detail(@PathVariable String slug) {
        return articleReadService.articleDetail(slug);
    }
}
