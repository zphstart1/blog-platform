package com.blog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 评论提交请求 DTO
 */
@Data
@NoArgsConstructor
public class CommentCreateRequest {

    @NotBlank(message = "评论者名称不能为空")
    @Size(min = 1, max = 20, message = "评论者名称长度需在1-20字符之间")
    private String authorName;

    private String authorEmail;

    private String authorWebsite;

    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 1000, message = "评论内容长度需在1-1000字符之间")
    private String content;

    private Long parentId;
}