package com.blog.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传结果 VO
 */
@Data
@NoArgsConstructor
public class UploadVO {

    private String url;
    private String originalName;
    private long size;
}