package com.blog.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应 VO — 包含 JWT Token 和用户信息
 */
@Data
@NoArgsConstructor
public class LoginVO {

    private String token;
    private UserVO user;
}