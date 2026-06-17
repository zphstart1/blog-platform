package com.blog.article.interfaces;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.article.application.ArticleApplicationService;
import com.blog.article.application.ArticleReadService;
import com.blog.article.application.command.CreateArticleCommand;
import com.blog.article.application.command.UpdateArticleCommand;
import com.blog.article.infrastructure.ArticleMapper;
import com.blog.dto.PageResult;
import com.blog.shared.Result;
import com.blog.vo.ArticleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 管理端文章接口 — DDD interfaces 层
 */
@Slf4j
@RestController
@RequestMapping("/admin/articles")
@RequiredArgsConstructor
public class AdminArticleController {

    private final ArticleApplicationService articleApplicationService;
    private final ArticleReadService articleReadService;
    private final ArticleMapper articleMapper;

    /** 管理端文章列表 */
    @GetMapping
    public Result<PageResult<ArticleVO>> list(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(required = false) String status,
                                               @RequestParam(required = false) Long categoryId,
                                               @RequestParam(required = false) Long authorId) {
        Page<ArticleVO> p = new Page<>(page, size);
        List<ArticleVO> records = articleMapper.selectAdminArticlePage(p, status, categoryId, authorId);
        List<ArticleVO> merged = ArticleReadService.mergeArticleVOs(records);
        PageResult<ArticleVO> result = PageResult.of(merged, p.getTotal(), page, size);
        return Result.ok(result);
    }

    /** 草稿列表 */
    @GetMapping("/drafts")
    public Result<PageResult<ArticleVO>> drafts(@RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return articleApplicationService.listDrafts(page, size);
    }

    /** 管理端文章详情（编辑回填） */
    @GetMapping("/{id}")
    public Result<ArticleVO> detail(@PathVariable Long id) {
        ArticleVO vo = articleMapper.selectAdminArticleById(id);
        if (vo == null) {
            return Result.fail(404, "文章不存在");
        }
        return Result.ok(vo);
    }

    @PostMapping
    public Result<Map<String, Object>> create(@Valid @RequestBody CreateArticleCommand cmd) {
        return articleApplicationService.createArticle(cmd);
    }

    @PutMapping("/{id}")
    public Result<Map<String, Object>> update(@PathVariable Long id, @RequestBody UpdateArticleCommand cmd) {
        return articleApplicationService.updateArticle(id, cmd);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return articleApplicationService.deleteArticle(id);
    }
}
