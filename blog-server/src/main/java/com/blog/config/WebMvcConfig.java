package com.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置 — 注册JWT拦截器
 * /api/admin/** 路径需要鉴权，/api/auth/** 和公开接口不需要鉴权
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    public WebMvcConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/admin/**", "/upload/**")
                .excludePathPatterns("/auth/**", "/articles", "/articles/**",
                        "/categories", "/tags", "/tags/**", "/search", "/comments/**");
    }
}