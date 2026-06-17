package com.blog.article.application;

import com.blog.article.domain.Link;
import com.blog.article.domain.LinkRepository;
import com.blog.shared.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 友链应用服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LinkApplicationService {

    private final LinkRepository linkRepository;

    public Result<List<Link>> listAll() {
        return Result.ok(linkRepository.findAllApproved());
    }

    public Result<List<Link>> adminList() {
        return Result.ok(linkRepository.findAll());
    }

    @Transactional
    public Result<Link> create(String name, String url, String logo, String description, int sortOrder) {
        Link link = Link.create(name, url, logo, description, sortOrder);
        link = linkRepository.save(link);
        log.info("创建友链: id={}, name={}", link.getId(), link.getName());
        return Result.ok(link);
    }

    @Transactional
    public Result<Link> update(Long id, String name, String url, String logo, String description, int sortOrder) {
        Optional<Link> opt = linkRepository.findById(id);
        if (opt.isEmpty()) {
            return Result.fail(404, "友链不存在");
        }

        Link link = opt.get();
        link.update(name, url, logo, description, sortOrder);
        linkRepository.update(link);

        log.info("更新友链: id={}", id);
        return Result.ok(link);
    }

    @Transactional
    public Result<Void> delete(Long id) {
        if (linkRepository.findById(id).isEmpty()) {
            return Result.fail(404, "友链不存在");
        }
        linkRepository.delete(id);
        log.info("删除友链: id={}", id);
        return Result.ok();
    }

    @Transactional
    public Result<Link> approve(Long id) {
        Optional<Link> opt = linkRepository.findById(id);
        if (opt.isEmpty()) {
            return Result.fail(404, "友链不存在");
        }
        Link link = opt.get();
        link.approve();
        linkRepository.update(link);
        return Result.ok(link);
    }
}
