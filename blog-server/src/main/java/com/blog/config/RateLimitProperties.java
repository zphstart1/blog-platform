package com.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 评论频率限制配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {

    private int commentVisitorPerMinute;
    private int commentUserPerMinute;
    private int loginFailPer15min;
}