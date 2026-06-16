package com.blog.controller;

import com.blog.common.Ret;
import com.blog.dto.CategoryRequest;
import com.blog.service.CategoryService;
import com.blog.vo.CategoryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 分类接口 — 公开列表 + 管理端 CRUD
 */
@Slf4j
@Api(tags = "分类接口")
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @ApiOperation("分类列表（公开）")
    @GetMapping("/categories")
    public Ret<List<CategoryVO>> list() {
        return categoryService.categoryList();
    }

    @ApiOperation("创建分类（管理端）")
    @PostMapping("/admin/categories")
    public Ret<CategoryVO> create(@Valid @RequestBody CategoryRequest request) {
        return categoryService.createCategory(request);
    }

    @ApiOperation("更新分类（管理端）")
    @PutMapping("/admin/categories/{id}")
    public Ret<CategoryVO> update(@PathVariable Long id, @RequestBody CategoryRequest request) {
        return categoryService.updateCategory(id, request);
    }

    @ApiOperation("删除分类（管理端）")
    @DeleteMapping("/admin/categories/{id}")
    public Ret<Void> delete(@PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }
}