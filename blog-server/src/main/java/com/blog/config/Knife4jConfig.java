package com.blog.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Knife4j API 文档配置 (OpenAPI 2 / springfox 模式)
 * 访问地址：http://localhost:8080/api/doc.html
 *
 * knife4j-openapi2-spring-boot-starter 4.3.0 基于 springfox。
 * 使用 @EnableKnife4j 增强文档，Docket 仍由 springfox 管理。
 * BUG-001修复: @EnableSwagger2WebMvc → @EnableKnife4j (springfox 3.0 已移除前者)
 */
@Configuration
@EnableKnife4j
@RequiredArgsConstructor
public class Knife4jConfig {

    private final OpenApiExtensionResolver openApiExtensionResolver;

    @Bean
    public Docket blogApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .apis(RequestHandlerSelectors.basePackage("com.blog.controller"))
                .paths(PathSelectors.any())
                .build()
                .extensions(openApiExtensionResolver.buildExtensions("Blog"));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("博客系统 API 文档")
                .description("前后端分离个人博客系统后端接口文档")
                .contact(new Contact("博主", "", "admin@blog.com"))
                .version("1.0.0")
                .build();
    }
}