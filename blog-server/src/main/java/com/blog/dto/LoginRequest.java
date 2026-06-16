package com.blog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

/**
 * 用户登录请求 DTO
 */
@Data
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}