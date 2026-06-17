package com.blog.article.interfaces;

import com.blog.article.application.CategoryApplicationService;
import com.blog.article.application.command.CreateCategoryCommand;
import com.blog.shared.Result;
import com.blog.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 分类接口 — DDD interfaces 层
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryApplicationService categoryApplicationService;

    @GetMapping("/categories")
    public Result<List<CategoryVO>> list() {
        return categoryApplicationService.listAll();
    }

    @PostMapping("/admin/categories")
    public Result<CategoryVO> create(@Valid @RequestBody CreateCategoryCommand cmd) {
        return categoryApplicationService.create(cmd);
    }

    @PutMapping("/admin/categories/{id}")
    public Result<CategoryVO> update(@PathVariable Long id, @Valid @RequestBody CreateCategoryCommand cmd) {
        return categoryApplicationService.update(id, cmd);
    }

    @DeleteMapping("/admin/categories/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return categoryApplicationService.delete(id);
    }
}
