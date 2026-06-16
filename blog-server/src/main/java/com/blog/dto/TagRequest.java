package com.blog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

/**
 * 标签创建/更新请求 DTO
 */
@Data
@NoArgsConstructor
public class TagRequest {

    @NotBlank(message = "标签名称不能为空")
    private String name;

    private String slug;
}