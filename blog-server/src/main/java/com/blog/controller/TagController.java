package com.blog.controller;

import com.blog.common.Ret;
import com.blog.dto.TagRequest;
import com.blog.service.TagService;
import com.blog.vo.TagVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 标签接口 — 公开列表/标签云 + 管理端 CRUD
 */
@Slf4j
@Api(tags = "标签接口")
@RestController
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @ApiOperation("标签列表（公开）")
    @GetMapping("/tags")
    public Ret<List<TagVO>> list() {
        return tagService.tagList();
    }

    @ApiOperation("标签云（公开）")
    @GetMapping("/tags/cloud")
    public Ret<List<TagVO>> cloud() {
        return tagService.tagCloud();
    }

    @ApiOperation("创建标签（管理端）")
    @PostMapping("/admin/tags")
    public Ret<TagVO> create(@Valid @RequestBody TagRequest request) {
        return tagService.createTag(request);
    }

    @ApiOperation("更新标签（管理端）")
    @PutMapping("/admin/tags/{id}")
    public Ret<TagVO> update(@PathVariable Long id, @RequestBody TagRequest request) {
        return tagService.updateTag(id, request);
    }

    @ApiOperation("删除标签（管理端）")
    @DeleteMapping("/admin/tags/{id}")
    public Ret<Void> delete(@PathVariable Long id) {
        return tagService.deleteTag(id);
    }
}