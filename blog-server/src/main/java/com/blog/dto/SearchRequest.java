package com.blog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 全文搜索请求 DTO
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SearchRequest extends PageRequest {

    @NotBlank(message = "搜索关键词不能为空")
    @Size(min = 1, max = 100, message = "关键词长度需在1-100字符之间")
    private String keyword;
}