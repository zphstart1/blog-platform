package com.blog.service;

import com.blog.common.ErrorCode;
import com.blog.common.Ret;
import com.blog.dto.TagRequest;
import com.blog.entity.Tag;
import com.blog.mapper.ArticleTagMapper;
import com.blog.mapper.TagMapper;
import com.blog.vo.TagVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 标签服务 — CRUD/标签云/文章计数/缓存
 */
@Slf4j
@Service
public class TagService {

    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public TagService(TagMapper tagMapper, ArticleTagMapper articleTagMapper,
                      RedisTemplate<String, Object> redisTemplate) {
        this.tagMapper = tagMapper;
        this.articleTagMapper = articleTagMapper;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 标签列表（含文章计数） — 缓存1h
     */
    public Ret<List<TagVO>> tagList() {
        String cacheKey = "blg:tag:all";
        List<TagVO> cached = (List<TagVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return Ret.ok(cached);
        }

        List<Tag> tags = tagMapper.selectList(null);
        List<TagVO> voList = tags.stream().map(t -> {
            TagVO vo = toTagVO(t);
            vo.setArticleCount(tagMapper.countArticlesByTagId(t.getId()));
            return vo;
        }).collect(Collectors.toList());

        redisTemplate.opsForValue().set(cacheKey, voList, 3600 + (long)(Math.random() * 600), TimeUnit.SECONDS);
        return Ret.ok(voList);
    }

    /**
     * 标签云 — weight = 文章数量
     */
    public Ret<List<TagVO>> tagCloud() {
        String cacheKey = "blg:tag:cloud";
        List<TagVO> cached = (List<TagVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return Ret.ok(cached);
        }

        List<Tag> tags = tagMapper.selectList(null);
        List<TagVO> voList = tags.stream().map(t -> {
            TagVO vo = new TagVO();
            vo.setId(t.getId());
            vo.setName(t.getName());
            vo.setSlug(t.getSlug());
            vo.setWeight(tagMapper.countArticlesByTagId(t.getId()));
            return vo;
        }).collect(Collectors.toList());

        redisTemplate.opsForValue().set(cacheKey, voList, 3600 + (long)(Math.random() * 600), TimeUnit.SECONDS);
        return Ret.ok(voList);
    }

    /**
     * 创建标签
     */
    public Ret<TagVO> createTag(TagRequest request) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, request.getName());
        if (tagMapper.selectCount(wrapper) > 0) {
            return Ret.fail(ErrorCode.TAG_EXISTS, "标签名称已存在");
        }

        Tag tag = new Tag();
        tag.setName(request.getName());
        tag.setSlug(StringUtils.hasText(request.getSlug()) ? request.getSlug() : request.getName().toLowerCase().replaceAll("\\s+", "-"));
        tagMapper.insert(tag);

        // 删除标签缓存
        redisTemplate.delete("blg:tag:all");
        redisTemplate.delete("blg:tag:cloud");
        log.info("创建标签: id={}, name={}", tag.getId(), tag.getName());

        TagVO vo = toTagVO(tag);
        return Ret.ok("创建成功", vo);
    }

    /**
     * 更新标签
     */
    public Ret<TagVO> updateTag(Long id, TagRequest request) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            return Ret.notFound("标签不存在");
        }

        if (StringUtils.hasText(request.getName())) {
            LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Tag::getName, request.getName());
            wrapper.ne(Tag::getId, id);
            if (tagMapper.selectCount(wrapper) > 0) {
                return Ret.fail(ErrorCode.TAG_EXISTS, "标签名称已存在");
            }
            tag.setName(request.getName());
        }
        if (StringUtils.hasText(request.getSlug())) {
            tag.setSlug(request.getSlug());
        }

        tagMapper.updateById(tag);
        redisTemplate.delete("blg:tag:all");
        redisTemplate.delete("blg:tag:cloud");
        log.info("更新标签: id={}", id);

        TagVO vo = toTagVO(tag);
        return Ret.ok("更新成功", vo);
    }

    /**
     * 删除标签
     */
    public Ret<Void> deleteTag(Long id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            return Ret.notFound("标签不存在");
        }

        // 删除关联
        LambdaQueryWrapper<com.blog.entity.ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.blog.entity.ArticleTag::getTagId, id);
        articleTagMapper.delete(wrapper);

        tagMapper.deleteById(id);
        redisTemplate.delete("blg:tag:all");
        redisTemplate.delete("blg:tag:cloud");
        log.info("删除标签: id={}", id);
        return Ret.ok("删除成功", null);
    }

    private TagVO toTagVO(Tag tag) {
        TagVO vo = new TagVO();
        vo.setId(tag.getId());
        vo.setName(tag.getName());
        vo.setSlug(tag.getSlug());
        return vo;
    }
}