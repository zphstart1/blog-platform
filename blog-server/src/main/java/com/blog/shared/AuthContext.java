package com.blog.shared;

/**
 * 当前用户上下文 — 替代原 CurrentUser 解析器，从请求中获取用户身份
 */
public class AuthContext {
    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    public static void set(LoginUser user) { HOLDER.set(user); }
    public static LoginUser get() { return HOLDER.get(); }
    public static Long getUserId() { return get() != null ? get().getId() : null; }
    public static boolean isAuthenticated() { return get() != null; }
    public static void clear() { HOLDER.remove(); }

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class LoginUser {
        private Long id;
        private String username;
        private String role;
    }
}
