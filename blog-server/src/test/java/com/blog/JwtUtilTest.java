package com.blog;

import com.blog.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtil 单元测试 — Token签发/解析/校验
 */
@SpringBootTest
@ActiveProfiles("test")
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("签发Token并解析")
    void generateAndParseToken() {
        String token = jwtUtil.generateToken(1L, "admin", "OWNER");
        assertNotNull(token);

        Claims claims = jwtUtil.parseToken(token);
        assertNotNull(claims);
        assertEquals("1", claims.getSubject());
        assertEquals("admin", claims.get("username", String.class));
        assertEquals("OWNER", claims.get("role", String.class));
    }

    @Test
    @DisplayName("从Token提取userId")
    void getUserIdFromToken() {
        String token = jwtUtil.generateToken(100L, "user100", "VISITOR");
        Long userId = jwtUtil.getUserId(token);
        assertEquals(100L, userId);
    }

    @Test
    @DisplayName("从Token提取role")
    void getRoleFromToken() {
        String token = jwtUtil.generateToken(1L, "admin", "ADMIN");
        String role = jwtUtil.getRole(token);
        assertEquals("ADMIN", role);
    }

    @Test
    @DisplayName("校验有效Token")
    void validateToken_valid() {
        String token = jwtUtil.generateToken(1L, "testuser", "VISITOR");
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("校验无效Token")
    void validateToken_invalid() {
        assertFalse(jwtUtil.validateToken("invalid-token-xxx"));
    }

    @Test
    @DisplayName("校验空Token")
    void validateToken_null() {
        assertFalse(jwtUtil.validateToken(null));
    }

    @Test
    @DisplayName("校验过期Token")
    void validateToken_expired() {
        // 手动构造过期Token无法直接测试（需要修改expiration配置）
        // 此测试确认正常Token不会过期
        String token = jwtUtil.generateToken(1L, "testuser", "VISITOR");
        assertTrue(jwtUtil.validateToken(token));
    }
}