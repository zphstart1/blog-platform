package com.blog;

import com.blog.common.Ret;
import com.blog.config.CurrentUser;
import com.blog.dto.*;
import com.blog.service.CategoryService;
import com.blog.service.TagService;
import com.blog.vo.CategoryVO;
import com.blog.vo.TagVO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 分类和标签 Service 单元测试 — 正常/异常/边界场景
 */
@SpringBootTest
@ActiveProfiles("test")
class CategoryAndTagServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;

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

    // ========== 分类正常场景 ==========

    @Test
    @DisplayName("分类列表-正常")
    void categoryList_success() {
        Ret<List<CategoryVO>> result = categoryService.categoryList();
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    @DisplayName("创建分类-正常")
    void createCategory_success() {
        CategoryRequest request = new CategoryRequest();
        request.setName("后端开发");
        request.setSlug("backend");
        request.setDescription("后端技术文章");

        Ret<CategoryVO> result = categoryService.createCategory(request);
        assertTrue(result.isSuccess());
        assertEquals("后端开发", result.getData().getName());
    }

    @Test
    @DisplayName("更新分类-正常")
    void updateCategory_success() {
        CategoryRequest createReq = new CategoryRequest();
        createReq.setName("前端开发");
        createReq.setSlug("frontend");
        Ret<CategoryVO> createResult = categoryService.createCategory(createReq);

        CategoryRequest updateReq = new CategoryRequest();
        updateReq.setName("前端开发更新");
        updateReq.setDescription("更新描述");

        Ret<CategoryVO> result = categoryService.updateCategory(createResult.getData().getId(), updateReq);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除分类-正常（无文章关联）")
    void deleteCategory_success() {
        CategoryRequest createReq = new CategoryRequest();
        createReq.setName("待删分类");
        createReq.setSlug("to-delete");
        Ret<CategoryVO> createResult = categoryService.createCategory(createReq);

        Ret<Void> result = categoryService.deleteCategory(createResult.getData().getId());
        assertTrue(result.isSuccess());
    }

    // ========== 分类异常场景 ==========

    @Test
    @DisplayName("创建分类-名称已存在")
    void createCategory_duplicateName() {
        CategoryRequest request = new CategoryRequest();
        request.setName("重复分类");
        request.setSlug("dup-cat");
        categoryService.createCategory(request);

        CategoryRequest dupRequest = new CategoryRequest();
        dupRequest.setName("重复分类");
        dupRequest.setSlug("dup-cat2");

        Ret<CategoryVO> result = categoryService.createCategory(dupRequest);
        assertFalse(result.isSuccess());
        assertEquals(1009, result.getCode());
    }

    @Test
    @DisplayName("更新分类-不存在")
    void updateCategory_notFound() {
        CategoryRequest updateReq = new CategoryRequest();
        updateReq.setName("不存在的分类");

        Ret<CategoryVO> result = categoryService.updateCategory(99999L, updateReq);
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    // ========== 标签正常场景 ==========

    @Test
    @DisplayName("标签列表-正常")
    void tagList_success() {
        Ret<List<TagVO>> result = tagService.tagList();
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    @DisplayName("标签云-正常")
    void tagCloud_success() {
        Ret<List<TagVO>> result = tagService.tagCloud();
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    @DisplayName("创建标签-正常")
    void createTag_success() {
        TagRequest request = new TagRequest();
        request.setName("Java");
        request.setSlug("java");

        Ret<TagVO> result = tagService.createTag(request);
        assertTrue(result.isSuccess());
        assertEquals("Java", result.getData().getName());
    }

    @Test
    @DisplayName("更新标签-正常")
    void updateTag_success() {
        TagRequest createReq = new TagRequest();
        createReq.setName("Spring");
        createReq.setSlug("spring");
        Ret<TagVO> createResult = tagService.createTag(createReq);

        TagRequest updateReq = new TagRequest();
        updateReq.setName("Spring Boot");

        Ret<TagVO> result = tagService.updateTag(createResult.getData().getId(), updateReq);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除标签-正常")
    void deleteTag_success() {
        TagRequest createReq = new TagRequest();
        createReq.setName("待删标签");
        createReq.setSlug("to-delete-tag");
        Ret<TagVO> createResult = tagService.createTag(createReq);

        Ret<Void> result = tagService.deleteTag(createResult.getData().getId());
        assertTrue(result.isSuccess());
    }

    // ========== 标签异常场景 ==========

    @Test
    @DisplayName("创建标签-名称已存在")
    void createTag_duplicateName() {
        TagRequest request = new TagRequest();
        request.setName("重复标签");
        request.setSlug("dup-tag");
        tagService.createTag(request);

        TagRequest dupRequest = new TagRequest();
        dupRequest.setName("重复标签");
        dupRequest.setSlug("dup-tag2");

        Ret<TagVO> result = tagService.createTag(dupRequest);
        assertFalse(result.isSuccess());
        assertEquals(1010, result.getCode());
    }

    @Test
    @DisplayName("更新标签-不存在")
    void updateTag_notFound() {
        TagRequest updateReq = new TagRequest();
        updateReq.setName("不存在的标签");

        Ret<TagVO> result = tagService.updateTag(99999L, updateReq);
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }
}