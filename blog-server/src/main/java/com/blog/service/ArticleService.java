package com.blog.service;

import com.blog.common.*;
import com.blog.config.CurrentUser;
import com.blog.dto.*;
import com.blog.entity.Article;
import com.blog.entity.ArticleTag;
import com.blog.entity.ArticleVersion;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.ArticleTagMapper;
import com.blog.mapper.ArticleVersionMapper;
import com.blog.mapper.CategoryMapper;
import com.blog.mapper.TagMapper;
import com.blog.utils.MarkdownUtil;
import com.blog.utils.SlugUtil;
import com.blog.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 文章服务 — CRUD/草稿管理/列表/详情/阅读量计数
 */
@Slf4j
@Service
public class ArticleService {

    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;
    private final ArticleVersionMapper articleVersionMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public ArticleService(ArticleMapper articleMapper, ArticleTagMapper articleTagMapper,
                          ArticleVersionMapper articleVersionMapper, CategoryMapper categoryMapper,
                          TagMapper tagMapper, RedisTemplate<String, Object> redisTemplate) {
        this.articleMapper = articleMapper;
        this.articleTagMapper = articleTagMapper;
        this.articleVersionMapper = articleVersionMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 公开文章列表 — 只返回 PUBLISHED 状态的文章
     */
    public Ret<PageResult<ArticleVO>> publicArticlePage(ArticlePageRequest request) {
        // Cache-Aside: 先查缓存
        String cacheKey = buildListCacheKey(request);
        PageResult<ArticleVO> cached = (PageResult<ArticleVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.debug("文章列表缓存命中: key={}", cacheKey);
            return Ret.ok(cached);
        }

        // 缓存未命中，查数据库
        Page<?> page = new Page<>(request.getPage(), request.getSize());
        String sortField = StringUtils.hasText(request.getSort()) ? request.getSort() : "publishedAt";
        String sortOrder = StringUtils.hasText(request.getOrder()) ? request.getOrder() : "desc";

        IPage<ArticleVO> result = articleMapper.selectPublicArticlePage(page,
                request.getCategoryId(), request.getTagId(), request.getKeyword(),
                sortField, sortOrder);

        PageResult<ArticleVO> pageResult = PageResult.of(result.getRecords(), result.getTotal(),
                request.getPage(), request.getSize());

        // 写入缓存，TTL 10min + 随机值防雪崩
        long ttl = 600 + (long) (Math.random() * 120);
        redisTemplate.opsForValue().set(cacheKey, pageResult, ttl, TimeUnit.SECONDS);

        return Ret.ok(pageResult);
    }

    /**
     * 文章详情 — 按 slug 查询，包含前后文章导航
     */
    public Ret<ArticleDetailVO> articleDetail(String slug) {
        // Cache-Aside: 先查缓存
        String cacheKey = "blg:article:detail:" + slug;
        ArticleDetailVO cached = (ArticleDetailVO) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.debug("文章详情缓存命中: key={}", cacheKey);
            // 阅读量计数
            incrementViewCount(cached.getId());
            return Ret.ok(cached);
        }

        // 查数据库
        ArticleVO articleVO = articleMapper.selectArticleDetailBySlug(slug);
        if (articleVO == null || !"PUBLISHED".equals(articleVO.getStatus())) {
            return Ret.notFound("文章不存在");
        }

        ArticleDetailVO detailVO = toDetailVO(articleVO);
        // 设置上下篇导航
        detailVO.setPrevArticle(findPrevArticle(articleVO.getId()));
        detailVO.setNextArticle(findNextArticle(articleVO.getId()));

        // 写入缓存，TTL 30min + 随机值
        long ttl = 1800 + (long) (Math.random() * 360);
        redisTemplate.opsForValue().set(cacheKey, detailVO, ttl, TimeUnit.SECONDS);

        // 阅读量计数
        incrementViewCount(articleVO.getId());

        return Ret.ok(detailVO);
    }

    /**
     * 管理端文章列表 — 包含 DRAFT 状态
     */
    public Ret<PageResult<ArticleVO>> adminArticlePage(AdminArticlePageRequest request) {
        CurrentUser currentUser = CurrentUser.get();
        Long authorId = null;
        // AUTHOR 只能看自己的文章
        if ("AUTHOR".equals(currentUser.getRole())) {
            authorId = currentUser.getId();
        }

        Page<?> page = new Page<>(request.getPage(), request.getSize());
        IPage<ArticleVO> result = articleMapper.selectAdminArticlePage(page,
                request.getStatus(), request.getCategoryId(), authorId);

        PageResult<ArticleVO> pageResult = PageResult.of(result.getRecords(), result.getTotal(),
                request.getPage(), request.getSize());
        return Ret.ok(pageResult);
    }

    /**
     * 管理端按ID获取单条文章 — 用于编辑回填，不限状态，AUTHOR仅可查看自己的
     */
    public Ret<ArticleDetailVO> adminArticleById(Long id) {
        CurrentUser currentUser = CurrentUser.get();

        ArticleVO articleVO = articleMapper.selectAdminArticleById(id);
        if (articleVO == null) {
            return Ret.notFound("文章不存在");
        }

        // 权限检查：AUTHOR 只能查看自己的文章
        if ("AUTHOR".equals(currentUser.getRole()) && !articleVO.getAuthor().getId().equals(currentUser.getId())) {
            return Ret.fail(ErrorCode.NO_EDIT_PERMISSION, "无权查看此文章");
        }

        ArticleDetailVO detailVO = toDetailVO(articleVO);
        // 编辑回填需要 categoryId 和 tagIds
        detailVO.setCategoryId(articleVO.getCategory() != null ? articleVO.getCategory().getId() : null);
        if (articleVO.getTags() != null) {
            detailVO.setTagIds(articleVO.getTags().stream().map(TagVO::getId).collect(Collectors.toList()));
        }

        log.info("管理端获取文章: id={}, status={}", id, articleVO.getStatus());
        return Ret.ok(detailVO);
    }

    /**
     * 创建文章 — 含 Markdown 渲染、slug 生成、标签关联
     */
    @Transactional(rollbackFor = Exception.class)
    public Ret<ArticleVO> createArticle(ArticleCreateRequest request) {
        CurrentUser currentUser = CurrentUser.get();

        // 发布文章时，内容不能为空
        if (ArticleStatus.PUBLISHED.getCode().equals(request.getStatus())
                && !StringUtils.hasText(request.getContent())) {
            return Ret.fail(ErrorCode.BAD_REQUEST, "发布文章时，内容不能为空");
        }

        // slug 处理：不传则自动生成，传入则校验唯一性
        String slug = StringUtils.hasText(request.getSlug()) ? request.getSlug() : SlugUtil.generateSlug(request.getTitle());
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getSlug, slug);
        if (articleMapper.selectCount(wrapper) > 0) {
            return Ret.fail(ErrorCode.SLUG_EXISTS, "slug 已存在");
        }

        // Markdown → HTML 渲染由前端负责，后端只存储原始 MD
        String summary = StringUtils.hasText(request.getSummary()) ? request.getSummary()
                : MarkdownUtil.extractSummary(request.getContent(), 200);

        // 创建文章
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setSlug(slug);
        article.setContent(request.getContent());
        article.setSummary(summary);
        article.setCoverImage(request.getCoverImage());
        article.setCategoryId(request.getCategoryId());
        article.setAuthorId(currentUser.getId());
        article.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus() : ArticleStatus.DRAFT.getCode());
        article.setIsTop(request.getIsTop() != null ? request.getIsTop() : 0);
        article.setViewCount(0);

        if (ArticleStatus.PUBLISHED.getCode().equals(article.getStatus())) {
            article.setPublishedAt(LocalDateTime.now());
        }

        articleMapper.insert(article);

        // 保存文章-标签关联
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            saveArticleTags(article.getId(), request.getTagIds());
        }

        // 删除列表缓存
        deleteListCache();

        log.info("创建文章: id={}, slug={}, status={}, authorId={}", article.getId(), slug, article.getStatus(), currentUser.getId());

        ArticleVO vo = new ArticleVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setSlug(article.getSlug());
        vo.setStatus(article.getStatus());
        vo.setPublishedAt(article.getPublishedAt());
        return Ret.ok("文章" + (ArticleStatus.PUBLISHED.getCode().equals(article.getStatus()) ? "发布" : "保存") + "成功", vo);
    }

    /**
     * 更新文章 — OWNER/ADMIN 可编辑任意文章，AUTHOR 仅可编辑自己的文章
     */
    @Transactional(rollbackFor = Exception.class)
    public Ret<ArticleVO> updateArticle(Long id, ArticleUpdateRequest request) {
        CurrentUser currentUser = CurrentUser.get();

        Article article = articleMapper.selectById(id);
        if (article == null) {
            return Ret.notFound("文章不存在");
        }

        // 权限检查：AUTHOR 只能编辑自己的文章
        if ("AUTHOR".equals(currentUser.getRole()) && !article.getAuthorId().equals(currentUser.getId())) {
            return Ret.fail(ErrorCode.NO_EDIT_PERMISSION, "无权编辑此文章");
        }

        // slug 唯一性校验（如果修改了 slug）
        if (StringUtils.hasText(request.getSlug()) && !request.getSlug().equals(article.getSlug())) {
            LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Article::getSlug, request.getSlug());
            if (articleMapper.selectCount(wrapper) > 0) {
                return Ret.fail(ErrorCode.SLUG_EXISTS, "slug 已存在");
            }
            article.setSlug(request.getSlug());
        }

        // 更新字段（只传需要更新的字段）
        if (StringUtils.hasText(request.getTitle())) {
            article.setTitle(request.getTitle());
        }
        if (StringUtils.hasText(request.getContent())) {
            article.setContent(request.getContent());
            if (!StringUtils.hasText(request.getSummary())) {
                article.setSummary(MarkdownUtil.extractSummary(request.getContent(), 200));
            }
        }
        if (StringUtils.hasText(request.getSummary())) {
            article.setSummary(request.getSummary());
        }
        if (request.getCoverImage() != null) {
            article.setCoverImage(request.getCoverImage());
        }
        if (request.getCategoryId() != null) {
            article.setCategoryId(request.getCategoryId());
        }
        if (request.getIsTop() != null) {
            article.setIsTop(request.getIsTop());
        }

        // 状态变更处理：DRAFT → PUBLISHED 需设置发布时间，并校验内容非空
        if (StringUtils.hasText(request.getStatus())) {
            if (ArticleStatus.PUBLISHED.getCode().equals(request.getStatus())) {
                // 检查更新后的内容是否非空（优先用请求中的，否则用已有的）
                String effectiveContent = StringUtils.hasText(request.getContent())
                        ? request.getContent() : article.getContent();
                if (!StringUtils.hasText(effectiveContent)) {
                    return Ret.fail(ErrorCode.BAD_REQUEST, "发布文章时，内容不能为空");
                }
                if (ArticleStatus.DRAFT.getCode().equals(article.getStatus())) {
                    article.setPublishedAt(LocalDateTime.now());
                }
            }
            article.setStatus(request.getStatus());
        }

        // BUG-004修复: 更新前保存版本快照到 article_version
        saveVersionSnapshot(id, article, currentUser.getId());

        articleMapper.updateById(article);

        // 更新标签关联
        if (request.getTagIds() != null) {
            // 删除旧关联
            LambdaQueryWrapper<ArticleTag> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(ArticleTag::getArticleId, id);
            articleTagMapper.delete(deleteWrapper);
            // 插入新关联
            saveArticleTags(id, request.getTagIds());
        }

        // 删除缓存
        deleteArticleCache(article.getSlug());

        log.info("更新文章: id={}, slug={}", id, article.getSlug());

        ArticleVO vo = new ArticleVO();
        vo.setId(id);
        vo.setTitle(article.getTitle());
        vo.setUpdatedAt(article.getUpdatedAt());
        return Ret.ok("文章更新成功", vo);
    }

    /**
     * 删除文章 — OWNER/ADMIN 可删除任意，AUTHOR 仅可删除自己的
     */
    @Transactional(rollbackFor = Exception.class)
    public Ret<Void> deleteArticle(Long id) {
        CurrentUser currentUser = CurrentUser.get();

        Article article = articleMapper.selectById(id);
        if (article == null) {
            return Ret.notFound("文章不存在");
        }

        if ("AUTHOR".equals(currentUser.getRole()) && !article.getAuthorId().equals(currentUser.getId())) {
            return Ret.fail(ErrorCode.NO_EDIT_PERMISSION, "无权删除此文章");
        }

        // 删除文章-标签关联
        LambdaQueryWrapper<ArticleTag> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.eq(ArticleTag::getArticleId, id);
        articleTagMapper.delete(tagWrapper);

        // 删除文章
        articleMapper.deleteById(id);

        // 删除缓存
        deleteArticleCache(article.getSlug());
        deleteListCache();

        log.info("删除文章: id={}, slug={}", id, article.getSlug());
        return Ret.ok("删除成功", null);
    }

    /**
     * 获取草稿列表 — 返回当前用户的草稿
     */
    public Ret<PageResult<ArticleVO>> draftList(PageRequest request) {
        CurrentUser currentUser = CurrentUser.get();
        Long authorId = currentUser.getId();

        Page<Article> page = new Page<>(request.getPage(), request.getSize());
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getAuthorId, authorId);
        wrapper.eq(Article::getStatus, ArticleStatus.DRAFT.getCode());
        wrapper.orderByDesc(Article::getUpdatedAt);

        IPage<Article> result = articleMapper.selectPage(page, wrapper);
        List<ArticleVO> voList = result.getRecords().stream().map(this::toArticleVOSimple).collect(Collectors.toList());
        PageResult<ArticleVO> pageResult = PageResult.of(voList, result.getTotal(), request.getPage(), request.getSize());
        return Ret.ok(pageResult);
    }

    // ========== 私有方法 ==========

    private void saveArticleTags(Long articleId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            ArticleTag articleTag = new ArticleTag();
            articleTag.setArticleId(articleId);
            articleTag.setTagId(tagId);
            articleTagMapper.insert(articleTag);
        }
    }

    private ArticleDetailVO toDetailVO(ArticleVO vo) {
        ArticleDetailVO detail = new ArticleDetailVO();
        detail.setId(vo.getId());
        detail.setTitle(vo.getTitle());
        detail.setSlug(vo.getSlug());
        detail.setContent(vo.getContent());
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
        return detail;
    }

    private ArticleVO toArticleVOSimple(Article article) {
        ArticleVO vo = new ArticleVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setSlug(article.getSlug());
        vo.setSummary(article.getSummary());
        vo.setCoverImage(article.getCoverImage());
        vo.setViewCount(article.getViewCount());
        vo.setIsTop(article.getIsTop() == 1);
        vo.setPublishedAt(article.getPublishedAt());
        vo.setStatus(article.getStatus());
        return vo;
    }

    private NavArticle findPrevArticle(Long currentId) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, ArticleStatus.PUBLISHED.getCode());
        wrapper.lt(Article::getId, currentId);
        wrapper.orderByDesc(Article::getId);
        wrapper.last("LIMIT 1");
        Article prev = articleMapper.selectOne(wrapper);
        if (prev == null) return null;
        NavArticle nav = new NavArticle();
        nav.setId(prev.getId());
        nav.setTitle(prev.getTitle());
        nav.setSlug(prev.getSlug());
        return nav;
    }

    private NavArticle findNextArticle(Long currentId) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, ArticleStatus.PUBLISHED.getCode());
        wrapper.gt(Article::getId, currentId);
        wrapper.orderByAsc(Article::getId);
        wrapper.last("LIMIT 1");
        Article next = articleMapper.selectOne(wrapper);
        if (next == null) return null;
        NavArticle nav = new NavArticle();
        nav.setId(next.getId());
        nav.setTitle(next.getTitle());
        nav.setSlug(next.getSlug());
        return nav;
    }

    private void incrementViewCount(Long articleId) {
        try {
            String dailyKey = "blg:article:view:daily:" + articleId + ":" + LocalDateTime.now().toLocalDate();
            Long count = redisTemplate.opsForValue().increment(dailyKey);
            redisTemplate.expire(dailyKey, 2, TimeUnit.DAYS);
            // BUG-003修复: 每10次访问批量写MySQL，使用自定义SQL直接累加 view_count
            if (count != null && count % 10 == 0) {
                articleMapper.incrementViewCount(articleId, 10);
                log.debug("阅读量批量写入MySQL: articleId={}, count={}", articleId, count);
            }
        } catch (Exception e) {
            log.warn("阅读量计数失败(Redis可能不可用): {}", e.getMessage());
        }
    }

    private String buildListCacheKey(ArticlePageRequest request) {
        return "blg:article:list:page:" + request.getPage() + ":size:" + request.getSize()
                + (request.getCategoryId() != null ? ":cat:" + request.getCategoryId() : "")
                + (request.getTagId() != null ? ":tag:" + request.getTagId() : "");
    }

    private void deleteArticleCache(String slug) {
        try {
            redisTemplate.delete("blg:article:detail:" + slug);
            redisTemplate.delete("blg:archive:all");
        } catch (Exception e) {
            log.warn("缓存删除失败: {}", e.getMessage());
        }
    }

    private void deleteListCache() {
        try {
            Set<String> keys = redisTemplate.keys("blg:article:list:*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.warn("列表缓存删除失败: {}", e.getMessage());
        }
    }

    /**
     * BUG-004修复: 文章更新前保存版本快照到 article_version 表
     * 记录更新前的 content/editorId/versionNo
     */
    private void saveVersionSnapshot(Long articleId, Article articleBeforeUpdate, Long editorId) {
        try {
            // 查询当前最大版本号
            Integer maxVersion = articleVersionMapper.selectMaxVersionNo(articleId);
            int nextVersion = (maxVersion != null ? maxVersion : 0) + 1;

            ArticleVersion version = new ArticleVersion();
            version.setArticleId(articleId);
            version.setVersionNo(nextVersion);
            version.setContent(articleBeforeUpdate.getContent());
            version.setEditorId(editorId);

            articleVersionMapper.insert(version);
            log.debug("文章版本快照已保存: articleId={}, versionNo={}", articleId, nextVersion);
        } catch (Exception e) {
            log.warn("保存文章版本快照失败(非致命): articleId={}, error={}", articleId, e.getMessage());
        }
    }
}