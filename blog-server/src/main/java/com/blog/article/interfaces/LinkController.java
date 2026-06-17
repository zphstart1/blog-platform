package com.blog.article.interfaces;

import com.blog.article.application.LinkApplicationService;
import com.blog.article.domain.Link;
import com.blog.shared.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 友链接口 — DDD interfaces 层
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class LinkController {

    private final LinkApplicationService linkApplicationService;

    /** 公开友链列表（已审核） */
    @GetMapping("/links")
    public Result<List<Link>> list() {
        return linkApplicationService.listAll();
    }

    /** 管理端友链列表（全部） */
    @GetMapping("/admin/links")
    public Result<List<Link>> adminList() {
        return linkApplicationService.adminList();
    }

    /** 创建友链 */
    @PostMapping("/admin/links")
    public Result<Link> create(@RequestBody Map<String, Object> req) {
        return linkApplicationService.create(
                (String) req.get("name"),
                (String) req.get("url"),
                (String) req.get("logo"),
                (String) req.get("description"),
                req.get("sortOrder") != null ? ((Number) req.get("sortOrder")).intValue() : 0
        );
    }

    /** 更新友链 */
    @PutMapping("/admin/links/{id}")
    public Result<Link> update(@PathVariable Long id, @RequestBody Map<String, Object> req) {
        return linkApplicationService.update(
                id,
                (String) req.get("name"),
                (String) req.get("url"),
                (String) req.get("logo"),
                (String) req.get("description"),
                req.get("sortOrder") != null ? ((Number) req.get("sortOrder")).intValue() : 0
        );
    }

    /** 删除友链 */
    @DeleteMapping("/admin/links/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return linkApplicationService.delete(id);
    }
}
