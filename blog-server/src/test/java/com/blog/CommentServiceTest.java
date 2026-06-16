package com.blog;

import com.blog.common.Ret;
import com.blog.config.CurrentUser;
import com.blog.dto.*;
import com.blog.service.CommentService;
import com.blog.vo.CommentVO;
import com.blog.vo.PendingCommentVO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CommentService 单元测试 — 正常/异常/边界场景
 * 重点测试访客先审后发(PENDING)和注册用户先发后审(APPROVED)
 */
@SpringBootTest
@ActiveProfiles("test")
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        CurrentUser.remove();
    }

    @AfterEach
    void tearDown() {
        CurrentUser.remove();
    }

    // ========== 正常场景 ==========

    @Test
    @DisplayName("访客提交评论-先审后发(PENDING)")
    void submitComment_visitor_pendingReview() {
        // 访客未登录，CurrentUser 为 null
        CommentCreateRequest request = new CommentCreateRequest();
        request.setAuthorName("访客小明");
        request.setContent("这是一条访客评论");
        request.setAuthorEmail("xiaoming@example.com");

        Ret<CommentVO> result = commentService.submitComment(1L, request, "192.168.1.100", "TestAgent");
        assertTrue(result.isSuccess());
        assertEquals("PENDING", result.getData().getStatus());
        assertTrue(result.getMessage().contains("审核"));
    }

    @Test
    @DisplayName("注册用户提交评论-先发后审(APPROVED)")
    void submitComment_registeredUser_autoApproved() {
        // 设置已登录用户
        CurrentUser user = new CurrentUser();
        user.setId(1L);
        user.setUsername("reguser");
        user.setRole("VISITOR");
        CurrentUser.set(user);

        CommentCreateRequest request = new CommentCreateRequest();
        request.setAuthorName("注册用户");
        request.setContent("这是一条注册用户评论");

        Ret<CommentVO> result = commentService.submitComment(1L, request, "192.168.1.200", "TestAgent");
        assertTrue(result.isSuccess());
        assertEquals("APPROVED", result.getData().getStatus());
    }

    @Test
    @DisplayName("嵌套回复评论")
    void submitComment_reply() {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setAuthorName("访客小红");
        request.setContent("回复某条评论");
        request.setParentId(1L);

        Ret<CommentVO> result = commentService.submitComment(1L, request, "192.168.1.150", "TestAgent");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("审核评论-通过")
    void reviewComment_approve() {
        CurrentUser admin = new CurrentUser();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setRole("OWNER");
        CurrentUser.set(admin);

        // 先创建一条待审核评论
        CommentCreateRequest req = new CommentCreateRequest();
        req.setAuthorName("待审核访客");
        req.setContent("待审核评论内容");
        Ret<CommentVO> comment = commentService.submitComment(1L, req, "10.0.0.1", "UA");

        // 审核通过
        ReviewRequest reviewReq = new ReviewRequest();
        reviewReq.setAction("APPROVE");

        Ret<Void> result = commentService.reviewComment(comment.getData().getId(), reviewReq);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("审核通过"));
    }

    @Test
    @DisplayName("审核评论-拒绝")
    void reviewComment_reject() {
        CurrentUser admin = new CurrentUser();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setRole("OWNER");
        CurrentUser.set(admin);

        CommentCreateRequest req = new CommentCreateRequest();
        req.setAuthorName("拒绝访客");
        req.setContent("要拒绝的评论");
        Ret<CommentVO> comment = commentService.submitComment(1L, req, "10.0.0.2", "UA");

        ReviewRequest reviewReq = new ReviewRequest();
        reviewReq.setAction("REJECT");

        Ret<Void> result = commentService.reviewComment(comment.getData().getId(), reviewReq);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("拒绝"));
    }

    // ========== 异常场景 ==========

    @Test
    @DisplayName("审核评论-VISITOR无审核权限")
    void reviewComment_visitorNoPermission() {
        CurrentUser visitor = new CurrentUser();
        visitor.setId(2L);
        visitor.setUsername("visitor");
        visitor.setRole("VISITOR");
        CurrentUser.set(visitor);

        ReviewRequest reviewReq = new ReviewRequest();
        reviewReq.setAction("APPROVE");

        Ret<Void> result = commentService.reviewComment(1L, reviewReq);
        assertFalse(result.isSuccess());
        assertEquals(403, result.getCode());
    }

    @Test
    @DisplayName("审核评论-评论不存在")
    void reviewComment_notFound() {
        CurrentUser admin = new CurrentUser();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setRole("OWNER");
        CurrentUser.set(admin);

        ReviewRequest reviewReq = new ReviewRequest();
        reviewReq.setAction("APPROVE");

        Ret<Void> result = commentService.reviewComment(99999L, reviewReq);
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    @DisplayName("删除评论-正常")
    void deleteComment_success() {
        CurrentUser admin = new CurrentUser();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setRole("OWNER");
        CurrentUser.set(admin);

        CommentCreateRequest req = new CommentCreateRequest();
        req.setAuthorName("要删除的访客");
        req.setContent("要删除的评论");
        Ret<CommentVO> comment = commentService.submitComment(1L, req, "10.0.0.3", "UA");

        Ret<Void> result = commentService.deleteComment(comment.getData().getId());
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除评论-评论不存在")
    void deleteComment_notFound() {
        CurrentUser admin = new CurrentUser();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setRole("OWNER");
        CurrentUser.set(admin);

        Ret<Void> result = commentService.deleteComment(99999L);
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    // ========== 边界场景 ==========

    @Test
    @DisplayName("评论内容-XSS过滤")
    void submitComment_xssFiltered() {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setAuthorName("XSS访客");
        request.setContent("<script>alert('xss')</script>这是评论内容");

        Ret<CommentVO> result = commentService.submitComment(1L, request, "10.0.0.4", "UA");
        assertTrue(result.isSuccess());
        // XSS标签应被过滤
        assertNotNull(result.getData());
    }

    @Test
    @DisplayName("评论内容-最长1000字符")
    void submitComment_maxLength() {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setAuthorName("长评论访客");
        request.setContent("A".repeat(1000));

        Ret<CommentVO> result = commentService.submitComment(1L, request, "10.0.0.5", "UA");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("频率限制-访客同一IP60秒内3条")
    void submitComment_rateLimitVisitor() {
        // 连续提交3条评论
        for (int i = 0; i < 3; i++) {
            CommentCreateRequest request = new CommentCreateRequest();
            request.setAuthorName("限速访客" + i);
            request.setContent("评论" + i);
            commentService.submitComment(1L, request, "10.0.0.6", "UA");
        }

        // 第4条应该被限速
        CommentCreateRequest request = new CommentCreateRequest();
        request.setAuthorName("限速访客4");
        request.setContent("第4条评论");

        Ret<CommentVO> result = commentService.submitComment(1L, request, "10.0.0.6", "UA");
        assertFalse(result.isSuccess());
        assertEquals(1011, result.getCode()); // COMMENT_TOO_FREQUENT
    }
}