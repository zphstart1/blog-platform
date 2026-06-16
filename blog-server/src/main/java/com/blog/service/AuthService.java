package com.blog.service;

import com.blog.common.ErrorCode;
import com.blog.common.Ret;
import com.blog.common.UserRole;
import com.blog.config.CurrentUser;
import com.blog.config.RateLimitProperties;
import com.blog.dto.LoginRequest;
import com.blog.dto.RegisterRequest;
import com.blog.entity.User;
import com.blog.mapper.UserMapper;
import com.blog.utils.JwtUtil;
import com.blog.vo.LoginVO;
import com.blog.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 认证服务 — 注册/登录/JWT签发/登录失败锁定
 */
@Slf4j
@Service
public class AuthService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;
    private final RateLimitProperties rateLimitProperties;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    public AuthService(UserMapper userMapper, JwtUtil jwtUtil,
                       StringRedisTemplate redisTemplate, RateLimitProperties rateLimitProperties) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
        this.rateLimitProperties = rateLimitProperties;
    }

    /**
     * 用户注册
     */
    public Ret<UserVO> register(RegisterRequest request) {
        // 用户名唯一性检查
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            log.warn("注册失败: 用户名已存在 {}", request.getUsername());
            return Ret.fail(ErrorCode.CONFLICT, "用户名已存在");
        }

        // 邮箱唯一性检查（如果提供了邮箱）
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, request.getEmail());
            if (userMapper.selectCount(wrapper) > 0) {
                log.warn("注册失败: 邮箱已存在 {}", request.getEmail());
                return Ret.fail(ErrorCode.CONFLICT, "邮箱已存在");
            }
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setRole(UserRole.VISITOR.getCode());
        user.setStatus(1);
        userMapper.insert(user);

        log.info("注册成功: userId={}, username={}", user.getId(), user.getUsername());

        UserVO vo = toUserVO(user);
        return Ret.ok("注册成功", vo);
    }

    /**
     * 用户登录 — 含登录失败次数锁定机制
     */
    public Ret<LoginVO> login(LoginRequest request) {
        // 检查登录失败锁定
        String lockKey = "blr:login:fail:" + request.getUsername();
        String failCountStr = redisTemplate.opsForValue().get(lockKey);
        if (failCountStr != null && Integer.parseInt(failCountStr) >= rateLimitProperties.getLoginFailPer15min()) {
            log.warn("登录锁定: username={}, failCount={}", request.getUsername(), failCountStr);
            return Ret.fail(ErrorCode.LOGIN_LOCKED, "登录过于频繁，请15分钟后重试");
        }

        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(wrapper);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // 登录失败，增加失败计数
            redisTemplate.opsForValue().increment(lockKey);
            redisTemplate.expire(lockKey, 15, TimeUnit.MINUTES);
            log.warn("登录失败: username={}", request.getUsername());
            return Ret.fail(ErrorCode.LOGIN_FAILED, "用户名或密码错误");
        }

        // 账号禁用检查
        if (user.getStatus() == 0) {
            log.warn("账号禁用: username={}", request.getUsername());
            return Ret.fail(ErrorCode.ACCOUNT_DISABLED, "账号已被禁用");
        }

        // 登录成功，清除失败计数，签发Token
        redisTemplate.delete(lockKey);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        log.info("登录成功: userId={}, username={}, role={}", user.getId(), user.getUsername(), user.getRole());

        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUser(toUserVO(user));
        return Ret.ok("登录成功", vo);
    }

    /**
     * 获取当前登录用户信息
     */
    public Ret<UserVO> getCurrentUser() {
        CurrentUser currentUser = CurrentUser.get();
        if (currentUser == null) {
            return Ret.unauthorized("未登录");
        }

        User user = userMapper.selectById(currentUser.getId());
        if (user == null) {
            return Ret.notFound("用户不存在");
        }
        return Ret.ok(toUserVO(user));
    }

    private UserVO toUserVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        vo.setEmail(user.getEmail());
        vo.setCreatedAt(user.getCreatedAt());
        return vo;
    }
}