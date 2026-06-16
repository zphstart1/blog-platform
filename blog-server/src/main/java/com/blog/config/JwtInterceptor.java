package com.blog.config;

import com.blog.common.Ret;
import com.blog.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT 认证拦截器 — 校验 Authorization: Bearer {token} 请求头
 * 解析成功后将用户信息存入 CurrentUser ThreadLocal
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public JwtInterceptor(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 请求直接放行（CORS预检）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeUnauthorizedResponse(response, "未登录或Token缺失");
            return false;
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            writeUnauthorizedResponse(response, "Token无效或已过期");
            return false;
        }

        // 解析Token，存入CurrentUser上下文
        Long userId = jwtUtil.getUserId(token);
        String username = jwtUtil.parseToken(token).get("username", String.class);
        String role = jwtUtil.getRole(token);

        CurrentUser currentUser = new CurrentUser();
        currentUser.setId(userId);
        currentUser.setUsername(username);
        currentUser.setRole(role);
        CurrentUser.set(currentUser);

        log.debug("JWT认证通过: userId={}, username={}, role={}", userId, username, role);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        CurrentUser.remove();
    }

    private void writeUnauthorizedResponse(HttpServletResponse response, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        Ret<Void> ret = Ret.unauthorized(message);
        response.getWriter().write(objectMapper.writeValueAsString(ret));
    }
}