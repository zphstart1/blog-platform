package com.blog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 用户注册请求 DTO
 */
@Data
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度需在4-20字符之间")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]*$", message = "用户名需字母开头，只允许字母数字下划线")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 30, message = "密码长度需在6-30字符之间")
    private String password;

    private String email;

    private String nickname;
}