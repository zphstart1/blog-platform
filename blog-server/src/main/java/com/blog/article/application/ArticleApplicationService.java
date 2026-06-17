package com.blog.article.application;

import com.blog.article.application.command.CreateArticleCommand;
import com.blog.article.application.command.UpdateArticleCommand;
import com.blog.article.domain.*;
import com.blog.article.infrastructure.ArticleMapper;
import com.blog.dto.PageResult;
import com.blog.shared.*;
import com.blog.utils.MarkdownUtil;
import com.blog.utils.SlugUtil;
import com.blog.vo.ArticleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 文章应用服务 — 编排 domain + infrastructure 完成用例
 */
@Slf4j
@Service
public class ArticleApplicationService {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final ArticleVersionRepository articleVersionRepository;
    private final ArticleDomainService domainService;
    private final ArticleMapper articleMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public ArticleApplicationService(ArticleRepository articleRepository,
                                      CategoryRepository categoryRepository,
                                      TagRepository tagRepository,
                                      ArticleVersionRepository articleVersionRepository,
                                      ArticleDomainService domainService,
                                      ArticleMapper articleMapper,
                                      RedisTemplate<String, Object> redisTemplate) {
        this.articleRepository = articleRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.articleVersionRepository = articleVersionRepository;
        this.domainService = domainService;
        this.articleMapper = articleMapper;
        this.redisTemplate = redisTemplate;
    }

    // ==================== 创建文章 ====================

    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> createArticle(CreateArticleCommand cmd) {
        AuthContext.LoginUser currentUser = AuthContext.get();

        // 确定发布状态
        ArticleStatus status = StringUtils.hasText(cmd.getStatus())
                ? ArticleStatus.fromCode(cmd.getStatus()) : ArticleStatus.DRAFT;

        // 发布时，内容不能为空
        if (status == ArticleStatus.PUBLISHED && !StringUtils.hasText(cmd.getContent())) {
            return Result.fail(400, "发布文章时，内容不能为空");
        }

        // slug 处理
        String slugValue = StringUtils.hasText(cmd.getSlug()) ? cmd.getSlug() : SlugUtil.generateSlug(cmd.getTitle());
        Slug slug = Slug.of(slugValue);
        if (articleRepository.existsBySlug(slug, null)) {
            return Result.fail(400, "slug 已存在");
        }

        // 创建文章领域对象
        Article article = Article.create(
                Title.of(cmd.getTitle()),
                slug,
                Content.of(cmd.getContent()),
                cmd.getSummary(),
                cmd.getCoverImage(),
                cmd.getCategoryId() != null ? CategoryId.of(cmd.getCategoryId()) : null,
                currentUser.getId(),
                status,
                cmd.getIsTop() != null && cmd.getIsTop() == 1
        );

        // 持久化
        article = articleRepository.save(article);

        // 保存标签关联
        if (cmd.getTagIds() != null && !cmd.getTagIds().isEmpty()) {
            List<TagId> tagIds = cmd.getTagIds().stream().map(TagId::of).collect(Collectors.toList());
            tagRepository.saveArticleTags(ArticleId.of(article.getId()), tagIds);
        }

        // 清除列表缓存
        deleteListCache();

        log.info("创建文章: id={}, slug={}, status={}, authorId={}",
                article.getId(), slugValue, status.getCode(), currentUser.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("id", article.getId());
        data.put("title", article.getTitle().value());
        data.put("slug", article.getSlug().value());
        data.put("status", article.getStatus().getCode());
        data.put("publishedAt", article.getPublishedAt());
        return Result.ok((Map<String, Object>) data);
    }

    // ==================== 更新文章 ====================

    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> updateArticle(Long id, UpdateArticleCommand cmd) {
        AuthContext.LoginUser currentUser = AuthContext.get();

        Optional<Article> optArticle = articleRepository.findById(ArticleId.of(id));
        if (optArticle.isEmpty()) {
            return Result.fail(404, "文章不存在");
        }
        Article article = optArticle.get();

        // 权限检查：AUTHOR 只能编辑自己的文章
        if ("AUTHOR".equals(currentUser.getRole()) && !article.getAuthorId().equals(currentUser.getId())) {
            return Result.fail(403, "无权编辑此文章");
        }

        // slug 唯一性校验（如果修改了 slug）
        if (StringUtils.hasText(cmd.getSlug()) && !cmd.getSlug().equals(article.getSlug().value())) {
            Slug newSlug = Slug.of(cmd.getSlug());
            if (articleRepository.existsBySlug(newSlug, ArticleId.of(id))) {
                return Result.fail(400, "slug 已存在");
            }
            article.changeSlug(newSlug);
        }

        // 更新字段
        if (StringUtils.hasText(cmd.getTitle())) {
            article.changeTitle(Title.of(cmd.getTitle()));
        }
        if (StringUtils.hasText(cmd.getContent())) {
            article.updateContent(Content.of(cmd.getContent()), cmd.getSummary());
        }
        if (StringUtils.hasText(cmd.getSummary())) {
            article.changeSummary(cmd.getSummary());
        }
        if (cmd.getCoverImage() != null) {
            article.changeCoverImage(cmd.getCoverImage());
        }
        if (cmd.getCategoryId() != null) {
            article.changeCategory(CategoryId.of(cmd.getCategoryId()));
        }
        if (cmd.getIsTop() != null) {
            article.setTop(cmd.getIsTop() == 1);
        }

        // 状态变更处理
        if (StringUtils.hasText(cmd.getStatus())) {
            ArticleStatus newStatus = ArticleStatus.fromCode(cmd.getStatus());
            if (newStatus == ArticleStatus.PUBLISHED && article.getStatus() == ArticleStatus.DRAFT) {
                // 校验内容非空
                if (StringUtils.hasText(cmd.getContent())) {
                    if (!StringUtils.hasText(cmd.getContent())) {
                        return Result.fail(400, "发布文章时，内容不能为空");
                    }
                }
                article.publish();
            } else if (newStatus == ArticleStatus.DRAFT && article.getStatus() == ArticleStatus.PUBLISHED) {
                article.unpublish();
            }
        }

        // 保存版本快照
        domainService.saveVersionSnapshot(article, currentUser.getId());

        // 持久化更新
        articleRepository.update(article);

        // 更新标签关联
        if (cmd.getTagIds() != null) {
            tagRepository.deleteArticleTags(ArticleId.of(id));
            if (!cmd.getTagIds().isEmpty()) {
                List<TagId> tagIds = cmd.getTagIds().stream().map(TagId::of).collect(Collectors.toList());
                tagRepository.saveArticleTags(ArticleId.of(id), tagIds);
            }
        }

        // 删除缓存
        deleteArticleCache(article.getSlug().value());

        log.info("更新文章: id={}, slug={}", id, article.getSlug().value());

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("title", article.getTitle().value());
        data.put("updatedAt", article.getUpdatedAt());
        return Result.ok(data);
    }

    // ==================== 删除文章 ====================

    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteArticle(Long id) {
        AuthContext.LoginUser currentUser = AuthContext.get();

        Optional<Article> optArticle = articleRepository.findById(ArticleId.of(id));
        if (optArticle.isEmpty()) {
            return Result.fail(404, "文章不存在");
        }
        Article article = optArticle.get();

        if ("AUTHOR".equals(currentUser.getRole()) && !article.getAuthorId().equals(currentUser.getId())) {
            return Result.fail(403, "无权删除此文章");
        }

        // 删除标签关联
        tagRepository.deleteArticleTags(ArticleId.of(id));

        // 删除版本快照
        articleVersionRepository.deleteByArticleId(ArticleId.of(id));

        // 删除文章
        articleRepository.delete(ArticleId.of(id));

        // 删除缓存
        deleteArticleCache(article.getSlug().value());
        deleteListCache();

        log.info("删除文章: id={}, slug={}", id, article.getSlug().value());
        return Result.ok();
    }

    // ==================== 草稿列表 ====================

    public Result<PageResult<ArticleVO>> listDrafts(int page, int size) {
        AuthContext.LoginUser currentUser = AuthContext.get();
        List<Article> drafts = articleRepository.findDraftsByAuthor(currentUser.getId(), page, size);
        long total = articleRepository.countDraftsByAuthor(currentUser.getId());

        List<ArticleVO> vos = drafts.stream().map(article -> {
            ArticleVO vo = new ArticleVO();
            vo.setId(article.getId());
            vo.setTitle(article.getTitle().value());
            vo.setSlug(article.getSlug().value());
            vo.setSummary(article.getSummary());
            vo.setCoverImage(article.getCoverImage());
            vo.setViewCount(article.getViewCount().value());
            vo.setIsTop(article.isTop());
            vo.setStatus(article.getStatus().getCode());
            vo.setPublishedAt(article.getPublishedAt());
            vo.setCreatedAt(article.getCreatedAt());
            vo.setUpdatedAt(article.getUpdatedAt());
            // 草稿列表不含分类/标签/作者详细信息，前端单独处理
            return vo;
        }).collect(Collectors.toList());

        PageResult<ArticleVO> result = PageResult.of(vos, total, page, size);
        return Result.ok(result);
    }

    // ==================== 读写查 ====================

    /**
     * 阅读量计数（写入 Redis，每10次回写 MySQL）
     */
    public void incrementViewCount(Long articleId) {
        try {
            String dailyKey = "blg:article:view:daily:" + articleId + ":" + java.time.LocalDateTime.now().toLocalDate();
            Long count = redisTemplate.opsForValue().increment(dailyKey);
            redisTemplate.expire(dailyKey, 2, TimeUnit.DAYS);
            if (count != null && count % 10 == 0) {
                articleRepository.incrementViewCountBulk(ArticleId.of(articleId), 10);
                log.debug("阅读量批量写入MySQL: articleId={}, count={}", articleId, count);
            }
        } catch (Exception e) {
            log.warn("阅读量计数失败(Redis可能不可用): {}", e.getMessage());
        }
    }

    // ==================== 缓存管理 ====================

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

    // ==================== 缓存 key 构建 ====================

    public static String buildListCacheKey(Long categoryId, Long tagId, int page, int size) {
        return "blg:article:list:page:" + page + ":size:" + size
                + (categoryId != null ? ":cat:" + categoryId : "")
                + (tagId != null ? ":tag:" + tagId : "");
    }

    public static String buildDetailCacheKey(String slug) {
        return "blg:article:detail:" + slug;
    }
}
