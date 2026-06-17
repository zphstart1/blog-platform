package com.blog.config;

import com.blog.user.infrastructure.UserMapper;
import com.blog.user.infrastructure.UserPO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 首次启动自动创建管理员账号
 * - 若 user 表中无 OWNER 角色用户，则自动创建
 * - 默认用户名: admin  密码: admin123
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;

    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin123";
    private static final String DEFAULT_EMAIL = "admin@blog.com";
    private static final String DEFAULT_NICKNAME = "博主";

    @Override
    public void run(String... args) {
        // 检查是否已存在 OWNER 用户
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getRole, "OWNER");
        Long count = userMapper.selectCount(wrapper);

        if (count != null && count > 0) {
            log.info("管理员账号已存在，跳过初始化");
            return;
        }

        // 创建默认管理员
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        UserPO admin = new UserPO();
        admin.setUsername(DEFAULT_USERNAME);
        admin.setPassword(encoder.encode(DEFAULT_PASSWORD));
        admin.setEmail(DEFAULT_EMAIL);
        admin.setNickname(DEFAULT_NICKNAME);
        admin.setRole("OWNER");
        admin.setStatus(1);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(admin);
        log.info("============================================");
        log.info("  默认管理员账号已创建");
        log.info("  用户名: {}", DEFAULT_USERNAME);
        log.info("  密  码: {}", DEFAULT_PASSWORD);
        log.info("  请登录后立即修改密码！");
        log.info("============================================");
    }
}
