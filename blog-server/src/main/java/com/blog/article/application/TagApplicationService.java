package com.blog.article.application;

import com.blog.article.application.command.CreateTagCommand;
import com.blog.article.domain.*;
import com.blog.shared.Result;
import com.blog.vo.TagVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 标签应用服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagApplicationService {

    private final TagRepository tagRepository;

    public Result<List<TagVO>> listAll() {
        List<Tag> tags = tagRepository.findAll();
        List<TagVO> vos = tags.stream().map(this::toTagVO).collect(Collectors.toList());
        return Result.ok(vos);
    }

    public Result<List<TagVO>> cloud() {
        List<Tag> tags = tagRepository.findAll();
        List<TagVO> vos = tags.stream()
                .map(this::toTagVO)
                .sorted((a, b) -> Integer.compare(b.getWeight() != null ? b.getWeight() : 0, a.getWeight() != null ? a.getWeight() : 0))
                .collect(Collectors.toList());
        return Result.ok(vos);
    }

    @Transactional
    public Result<TagVO> create(CreateTagCommand cmd) {
        // 名称唯一性校验
        if (tagRepository.existsByName(cmd.getName(), null)) {
            return Result.fail(400, "标签名称已存在");
        }

        Tag tag = Tag.create(cmd.getName(), Slug.of(cmd.getSlug()));
        tag = tagRepository.save(tag);

        log.info("创建标签: id={}, name={}", tag.getId(), tag.getName());
        TagVO vo = toTagVO(tag);
        vo.setArticleCount(0);
        vo.setWeight(0);
        return Result.ok(vo);
    }

    @Transactional
    public Result<TagVO> update(Long id, CreateTagCommand cmd) {
        Optional<Tag> opt = tagRepository.findById(TagId.of(id));
        if (opt.isEmpty()) {
            return Result.fail(404, "标签不存在");
        }

        Tag tag = opt.get();

        if (!tag.getName().equals(cmd.getName()) && tagRepository.existsByName(cmd.getName(), TagId.of(id))) {
            return Result.fail(400, "标签名称已存在");
        }

        tag.rename(cmd.getName());
        tag.changeSlug(Slug.of(cmd.getSlug()));
        tagRepository.update(tag);

        log.info("更新标签: id={}, name={}", id, tag.getName());
        return Result.ok(toTagVO(tag));
    }

    @Transactional
    public Result<Void> delete(Long id) {
        if (tagRepository.findById(TagId.of(id)).isEmpty()) {
            return Result.fail(404, "标签不存在");
        }
        tagRepository.delete(TagId.of(id));

        log.info("删除标签: id={}", id);
        return Result.ok();
    }

    private TagVO toTagVO(Tag tag) {
        TagVO vo = new TagVO();
        vo.setId(tag.getId());
        vo.setName(tag.getName());
        vo.setSlug(tag.getSlug().value());
        int articleCount = tagRepository.countArticlesByTagId(TagId.of(tag.getId()));
        vo.setArticleCount(articleCount);
        vo.setWeight(articleCount);
        return vo;
    }
}
