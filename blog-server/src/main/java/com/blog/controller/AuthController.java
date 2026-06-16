package com.blog.controller;

import com.blog.common.Ret;
import com.blog.dto.LoginRequest;
import com.blog.dto.RegisterRequest;
import com.blog.service.AuthService;
import com.blog.vo.LoginVO;
import com.blog.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器 — 注册/登录/获取当前用户
 * 不需要鉴权（WebMvcConfig已排除 /auth/**）
 */
@Slf4j
@Api(tags = "认证接口")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Ret<UserVO> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Ret<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @ApiOperation("获取当前登录用户信息")
    @GetMapping("/me")
    public Ret<UserVO> me() {
        return authService.getCurrentUser();
    }
}