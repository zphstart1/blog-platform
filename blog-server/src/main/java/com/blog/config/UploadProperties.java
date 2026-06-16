package com.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 图片上传存储配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "upload")
public class UploadProperties {

    private String imagePath;
    private String allowedTypes;
    private long maxSize;
}