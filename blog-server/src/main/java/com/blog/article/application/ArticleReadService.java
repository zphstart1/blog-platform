package com.blog.article.application;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.article.infrastructure.ArticleMapper;
import com.blog.shared.Result;
import com.blog.dto.ArticlePageRequest;
import com.blog.dto.PageResult;
import com.blog.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文章读服务 — 公开读侧，直接使用 ArticleMapper XML 查询
 */
@Slf4j
@Service
public class ArticleReadService {

    private final ArticleMapper articleMapper;

    public ArticleReadService(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    /** 公开文章分页列表 */
    public Result<PageResult<ArticleVO>> publicArticlePage(ArticlePageRequest request) {
        Page<ArticleVO> page = new Page<>(request.getPage(), request.getSize());

        String sortField = "publishedAt";
        String sortOrder = "desc";
        if (request.getSort() != null) {
            sortField = request.getSort();
        }
        if (request.getOrder() != null) {
            sortOrder = request.getOrder();
        }

        List<ArticleVO> rawList = articleMapper.selectPublicArticlePage(
                page,
                request.getCategoryId(),
                request.getTagId(),
                request.getKeyword(),
                sortField,
                sortOrder
        );

        // 聚合标签（MyBatis 一对多会返回多行，需要手动合并）
        List<ArticleVO> mergedList = mergeArticleVOs(rawList);

        PageResult<ArticleVO> result = PageResult.of(
                mergedList,
                page.getTotal(),
                request.getPage(),
                request.getSize()
        );
        return Result.ok(result);
    }

    /** 文章详情 */
    public Result<ArticleDetailVO> articleDetail(String slug) {
        List<ArticleVO> list = articleMapper.selectArticleDetailBySlug(slug);
        if (list.isEmpty()) {
            return Result.fail(404, "文章不存在");
        }

        List<ArticleVO> merged = mergeArticleVOs(list);
        ArticleVO vo = merged.get(0);

        ArticleDetailVO detail = new ArticleDetailVO();
        detail.setId(vo.getId());
        detail.setTitle(vo.getTitle());
        detail.setSlug(vo.getSlug());
        detail.setContent(vo.getContent());
        detail.setContentHtml(vo.getContentHtml());
        detail.setSummary(vo.getSummary());
        detail.setCoverImage(vo.getCoverImage());
        detail.setCategory(vo.getCategory());
        detail.setTags(vo.getTags());
        detail.setAuthor(vo.getAuthor());
        detail.setViewCount(vo.getViewCount());
        detail.setIsTop(vo.getIsTop());
        detail.setPublishedAt(vo.getPublishedAt());
        detail.setCreatedAt(vo.getCreatedAt());
        detail.setUpdatedAt(vo.getUpdatedAt());
        detail.setCategoryId(vo.getCategory() != null ? vo.getCategory().getId() : null);
        detail.setTagIds(vo.getTags() != null ? vo.getTags().stream().map(TagVO::getId).collect(Collectors.toList()) : Collections.emptyList());

        // 上一篇/下一篇
        if (vo.getId() != null) {
            detail.setPrevArticle(toNavArticle(articleMapper.findPrevPublished(vo.getId())));
            detail.setNextArticle(toNavArticle(articleMapper.findNextPublished(vo.getId())));
        }

        return Result.ok(detail);
    }

    // ==================== 工具方法 ====================

    /** 合并 MyBatis 一对多查询返回的多行（按文章ID去重，合并标签） */
    public static List<ArticleVO> mergeArticleVOs(List<ArticleVO> rawList) {
        if (rawList == null || rawList.isEmpty()) return Collections.emptyList();

        Map<Long, ArticleVO> map = new LinkedHashMap<>();
        for (ArticleVO vo : rawList) {
            ArticleVO existing = map.get(vo.getId());
            if (existing == null) {
                map.put(vo.getId(), vo);
                // 初始化标签列表
                if (vo.getTags() == null) {
                    vo.setTags(new ArrayList<>());
                }
            } else {
                // 合并标签
                List<TagVO> tags = existing.getTags();
                if (vo.getTags() != null) {
                    for (TagVO tag : vo.getTags()) {
                        if (tag.getId() != null && tags.stream().noneMatch(t -> tag.getId().equals(t.getId()))) {
                            tags.add(tag);
                        }
                    }
                }
            }
        }
        return new ArrayList<>(map.values());
    }

    private NavArticle toNavArticle(Optional<com.blog.article.infrastructure.ArticlePO> po) {
        return po.map(p -> {
            NavArticle nav = new NavArticle();
            nav.setId(p.getId());
            nav.setTitle(p.getTitle());
            nav.setSlug(p.getSlug());
            return nav;
        }).orElse(null);
    }
}
