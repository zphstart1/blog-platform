package com.blog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

/**
 * 评论审核请求 DTO
 */
@Data
@NoArgsConstructor
public class ReviewRequest {

    @NotBlank(message = "审核操作不能为空")
    private String action;
}