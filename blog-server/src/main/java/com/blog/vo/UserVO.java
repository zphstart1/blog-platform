package com.blog.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 用户信息 VO — 登录/注册响应中的用户数据
 */
@Data
@NoArgsConstructor
public class UserVO {

    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String role;
    private String email;
    private LocalDateTime createdAt;
}