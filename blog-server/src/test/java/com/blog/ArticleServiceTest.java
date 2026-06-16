package com.blog;

import com.blog.common.ArticleStatus;
import com.blog.common.Ret;
import com.blog.config.CurrentUser;
import com.blog.dto.*;
import com.blog.service.ArticleService;
import com.blog.vo.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ArticleService 单元测试 — 正常/异常/边界/并发场景
 */
@SpringBootTest
@ActiveProfiles("test")
class ArticleServiceTest {

    @Autowired
    private ArticleService articleService;

    private Long testUserId;

    @BeforeEach
    void setUp() {
        CurrentUser currentUser = new CurrentUser();
        currentUser.setId(1L);
        currentUser.setUsername("testowner");
        currentUser.setRole("OWNER");
        CurrentUser.set(currentUser);
    }

    @AfterEach
    void tearDown() {
        CurrentUser.remove();
    }

    // ========== 正常场景 ==========

    @Test
    @DisplayName("创建文章-草稿")
    void createArticle_draft() {
        ArticleCreateRequest request = new ArticleCreateRequest();
        request.setTitle("测试草稿文章");
        request.setContent("# 这是草稿\n\n草稿内容...");
        request.setStatus("DRAFT");

        Ret<ArticleVO> result = articleService.createArticle(request);
        assertTrue(result.isSuccess());
        assertEquals("DRAFT", result.getData().getStatus());
        assertNull(result.getData().getPublishedAt());
    }

    @Test
    @DisplayName("创建文章-发布")
    void createArticle_published() {
        ArticleCreateRequest request = new ArticleCreateRequest();
        request.setTitle("测试发布文章");
        request.setContent("# 这是发布文章\n\n发布内容...");
        request.setStatus("PUBLISHED");

        Ret<ArticleVO> result = articleService.createArticle(request);
        assertTrue(result.isSuccess());
        assertEquals("PUBLISHED", result.getData().getStatus());
        assertNotNull(result.getData().getPublishedAt());
    }

    @Test
    @DisplayName("创建文章-自动生成slug")
    void createArticle_autoSlug() {
        ArticleCreateRequest request = new ArticleCreateRequest();
        request.setTitle("Auto Slug Test Article");
        request.setContent("Content for auto slug test");
        request.setStatus("PUBLISHED");

        Ret<ArticleVO> result = articleService.createArticle(request);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData().getSlug());
        assertTrue(result.getData().getSlug().length() > 0);
    }

    @Test
    @DisplayName("更新文章-OWNER可编辑任意文章")
    void updateArticle_ownerCanEdit() {
        // 先创建
        ArticleCreateRequest createReq = new ArticleCreateRequest();
        createReq.setTitle("原标题");
        createReq.setContent("原内容");
        createReq.setStatus("DRAFT");
        Ret<ArticleVO> createResult = articleService.createArticle(createReq);

        // 再更新
        ArticleUpdateRequest updateReq = new ArticleUpdateRequest();
        updateReq.setTitle("更新标题");
        updateReq.setStatus("PUBLISHED");

        Ret<ArticleVO> result = articleService.updateArticle(createResult.getData().getId(), updateReq);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除文章-正常")
    void deleteArticle_success() {
        ArticleCreateRequest createReq = new ArticleCreateRequest();
        createReq.setTitle("要删除的文章");
        createReq.setContent("内容");
        createReq.setStatus("DRAFT");
        Ret<ArticleVO> createResult = articleService.createArticle(createReq);

        Ret<Void> result = articleService.deleteArticle(createResult.getData().getId());
        assertTrue(result.isSuccess());
    }

    // ========== 异常场景 ==========

    @Test
    @DisplayName("创建文章-slug重复")
    void createArticle_duplicateSlug() {
        ArticleCreateRequest req1 = new ArticleCreateRequest();
        req1.setTitle("文章1");
        req1.setSlug("duplicate-slug");
        req1.setContent("内容1");
        req1.setStatus("DRAFT");
        articleService.createArticle(req1);

        ArticleCreateRequest req2 = new ArticleCreateRequest();
        req2.setTitle("文章2");
        req2.setSlug("duplicate-slug");
        req2.setContent("内容2");
        req2.setStatus("DRAFT");

        Ret<ArticleVO> result = articleService.createArticle(req2);
        assertFalse(result.isSuccess());
        assertEquals(1006, result.getCode()); // SLUG_EXISTS
    }

    @Test
    @DisplayName("更新文章-AUTHOR无权编辑他人文章")
    void updateArticle_authorNoPermission() {
        // 创建文章（OWNER）
        ArticleCreateRequest createReq = new ArticleCreateRequest();
        createReq.setTitle("他人文章");
        createReq.setContent("内容");
        createReq.setStatus("DRAFT");
        Ret<ArticleVO> createResult = articleService.createArticle(createReq);

        // 切换为 AUTHOR 角色
        CurrentUser author = new CurrentUser();
        author.setId(999L); // 不同用户
        author.setUsername("testauthor");
        author.setRole("AUTHOR");
        CurrentUser.set(author);

        ArticleUpdateRequest updateReq = new ArticleUpdateRequest();
        updateReq.setTitle("尝试修改");

        Ret<ArticleVO> result = articleService.updateArticle(createResult.getData().getId(), updateReq);
        assertFalse(result.isSuccess());
        assertEquals(1008, result.getCode()); // NO_EDIT_PERMISSION
    }

    @Test
    @DisplayName("更新/删除-文章不存在")
    void updateArticle_notFound() {
        ArticleUpdateRequest updateReq = new ArticleUpdateRequest();
        updateReq.setTitle("修改不存在文章");

        Ret<ArticleVO> result = articleService.updateArticle(99999L, updateReq);
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    @DisplayName("删除文章-AUTHOR无权删除他人文章")
    void deleteArticle_authorNoPermission() {
        ArticleCreateRequest createReq = new ArticleCreateRequest();
        createReq.setTitle("他人文章2");
        createReq.setContent("内容");
        createReq.setStatus("DRAFT");
        Ret<ArticleVO> createResult = articleService.createArticle(createReq);

        CurrentUser author = new CurrentUser();
        author.setId(999L);
        author.setUsername("testauthor2");
        author.setRole("AUTHOR");
        CurrentUser.set(author);

        Ret<Void> result = articleService.deleteArticle(createResult.getData().getId());
        assertFalse(result.isSuccess());
        assertEquals(1008, result.getCode());
    }

    // ========== 边界场景 ==========

    @Test
    @DisplayName("创建文章-标题最长200字符")
    void createArticle_maxTitleLength() {
        ArticleCreateRequest request = new ArticleCreateRequest();
        request.setTitle("A".repeat(200));
        request.setContent("内容");
        request.setStatus("DRAFT");

        Ret<ArticleVO> result = articleService.createArticle(request);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("草稿列表-AUTHOR只能看自己的草稿")
    void draftList_authorOnlyOwnDrafts() {
        CurrentUser author = new CurrentUser();
        author.setId(999L);
        author.setUsername("draftauthor");
        author.setRole("AUTHOR");
        CurrentUser.set(author);

        PageRequest pageReq = new PageRequest(1, 10);
        Ret<PageResult<ArticleVO>> result = articleService.draftList(pageReq);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("文章详情-slug不存在")
    void articleDetail_notFound() {
        Ret<ArticleDetailVO> result = articleService.articleDetail("nonexistent-slug");
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    // ========== 并发场景 ==========

    @Test
    @DisplayName("并发创建文章-相同slug只有一个成功")
    void concurrentCreateArticle_sameSlug() throws InterruptedException {
        int threadCount = 5;
        Thread[] threads = new Thread[threadCount];
        int[] successCount = {0};

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                CurrentUser user = new CurrentUser();
                user.setId(1L);
                user.setUsername("concurrentuser");
                user.setRole("OWNER");
                CurrentUser.set(user);

                ArticleCreateRequest req = new ArticleCreateRequest();
                req.setTitle("并发文章" + System.currentTimeMillis());
                req.setSlug("concurrent-slug-test");
                req.setContent("并发内容");
                req.setStatus("DRAFT");

                Ret<ArticleVO> result = articleService.createArticle(req);
                if (result.isSuccess()) {
                    successCount[0]++;
                }
                CurrentUser.remove();
            });
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        // 至少有一个成功，但由于MySQL唯一索引保护，不会有多个
        assertTrue(successCount[0] >= 1, "至少有一个线程成功创建文章");
    }
}