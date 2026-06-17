package com.blog.article.application.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 创建标签命令
 */
@Data
public class CreateTagCommand {
    @NotBlank(message = "标签名称不能为空")
    private String name;
    private String slug;
}
