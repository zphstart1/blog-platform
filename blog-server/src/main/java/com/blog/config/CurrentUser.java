package com.blog.config;

import lombok.Data;

/**
 * 当前登录用户上下文 — 从 JWT Token 中提取的用户信息
 * 通过 JwtInterceptor 解析后存入 ThreadLocal
 */
@Data
public class CurrentUser {

    private Long id;
    private String username;
    private String role;

    private static final ThreadLocal<CurrentUser> CONTEXT = new ThreadLocal<>();

    public static void set(CurrentUser user) {
        CONTEXT.set(user);
    }

    public static CurrentUser get() {
        return CONTEXT.get();
    }

    public static void remove() {
        CONTEXT.remove();
    }

    /**
     * 判断当前用户是否拥有管理权限
     */
    public boolean isManager() {
        return "OWNER".equals(role) || "ADMIN".equals(role) || "AUTHOR".equals(role);
    }

    /**
     * 判断当前用户是否拥有审核权限
     */
    public boolean canReview() {
        return "OWNER".equals(role) || "ADMIN".equals(role);
    }
}