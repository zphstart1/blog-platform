package com.blog.user.application;

import com.blog.shared.AuthContext;
import com.blog.shared.Result;
import com.blog.user.domain.*;
import com.blog.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 认证应用服务 — 注册/登录/JWT签发
 */
@Slf4j
@Service
public class AuthApplicationService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    public AuthApplicationService(UserRepository userRepository, JwtUtil jwtUtil, StringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public Result<Object> register(String username, String password, String email, String nickname) {
        Username uname = Username.of(username);
        if (userRepository.existsByUsername(uname)) {
            return Result.fail(409, "用户名已存在");
        }
        Email mail = Email.of(email);
        if (userRepository.existsByEmail(mail)) {
            return Result.fail(409, "邮箱已注册");
        }

        User user = User.register(uname, password, mail);
        if (nickname != null && !nickname.isEmpty()) user.changeNickname(nickname);
        userRepository.save(user);

        log.info("用户注册: id={}, username={}", user.getId(), username);
        return Result.ok("注册成功", userInfo(user));
    }

    public Result<Object> login(String username, String password) {
        Username uname = Username.of(username);
        User user = userRepository.findByUsername(uname).orElse(null);
        if (user == null) {
            return Result.fail(401, "用户名或密码错误");
        }
        if (!user.verifyPassword(password)) {
            // 登录失败计数
            String failKey = "login:fail:" + username;
            Long fails = redisTemplate.opsForValue().increment(failKey);
            redisTemplate.expire(failKey, 15, TimeUnit.MINUTES);
            if (fails != null && fails >= 5) {
                return Result.fail(429, "登录失败次数过多，请15分钟后再试");
            }
            return Result.fail(401, "用户名或密码错误");
        }
        if (!user.isEnabled()) {
            return Result.fail(403, "账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername().value(), user.getRole().name());
        log.info("用户登录: id={}, username={}", user.getId(), username);

        var result = new java.util.HashMap<String, Object>();
        result.put("token", token);
        result.put("user", userInfo(user));
        return Result.ok("登录成功", result);
    }

    public Result<Object> getCurrentUser() {
        AuthContext.LoginUser loginUser = AuthContext.get();
        if (loginUser == null) return Result.fail(401, "未登录");

        User user = userRepository.findById(UserId.of(loginUser.getId())).orElse(null);
        if (user == null) return Result.fail(404, "用户不存在");

        return Result.ok(userInfo(user));
    }

    private java.util.Map<String, Object> userInfo(User user) {
        var m = new java.util.HashMap<String, Object>();
        m.put("id", user.getId());
        m.put("username", user.getUsername().value());
        m.put("email", user.getEmail().value());
        m.put("nickname", user.getNickname());
        m.put("avatar", user.getAvatar());
        m.put("role", user.getRole().name());
        return m;
    }
}
