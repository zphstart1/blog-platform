package com.blog.article.interfaces;

import com.blog.article.application.TagApplicationService;
import com.blog.article.application.command.CreateTagCommand;
import com.blog.shared.Result;
import com.blog.vo.TagVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 标签接口 — DDD interfaces 层
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TagController {

    private final TagApplicationService tagApplicationService;

    @GetMapping("/tags")
    public Result<List<TagVO>> list() {
        return tagApplicationService.listAll();
    }

    @GetMapping("/tags/cloud")
    public Result<List<TagVO>> cloud() {
        return tagApplicationService.cloud();
    }

    @PostMapping("/admin/tags")
    public Result<TagVO> create(@Valid @RequestBody CreateTagCommand cmd) {
        return tagApplicationService.create(cmd);
    }

    @PutMapping("/admin/tags/{id}")
    public Result<TagVO> update(@PathVariable Long id, @Valid @RequestBody CreateTagCommand cmd) {
        return tagApplicationService.update(id, cmd);
    }

    @DeleteMapping("/admin/tags/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return tagApplicationService.delete(id);
    }
}
