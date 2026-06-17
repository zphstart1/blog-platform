package com.blog.user.domain;

import com.blog.shared.BaseEntity;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

/**
 * 用户聚合根 — user 限界上下文
 */
@Getter
public class User extends BaseEntity {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder(10);

    private Username username;
    private String password; // BCrypt 加密后
    private Email email;
    private String nickname;
    private String avatar;
    private Role role;
    private int status; // 0=禁用, 1=正常

    protected User() {}

    public static User register(Username username, String rawPassword, Email email) {
        User user = new User();
        user.username = username;
        user.password = ENCODER.encode(rawPassword);
        user.email = email;
        user.nickname = username.value();
        user.avatar = null;
        user.role = Role.AUTHOR;
        user.status = 1;
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    public boolean verifyPassword(String rawPassword) {
        return ENCODER.matches(rawPassword, this.password);
    }

    public void changePassword(String rawPassword) {
        this.password = ENCODER.encode(rawPassword);
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changeAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void disable() { this.status = 0; }
    public void enable() { this.status = 1; }
    public boolean isEnabled() { return this.status == 1; }

    // Getters provided by Lombok @Getter

    // ===== 重建 =====
    public static User reconstruct(Long id, Username username, String password, Email email,
                                    String nickname, String avatar, Role role, int status,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        User u = new User();
        u.username = username;
        u.password = password;
        u.email = email;
        u.nickname = nickname;
        u.avatar = avatar;
        u.role = role;
        u.status = status;
        setBaseId(u, id);
        u.setCreatedAt(createdAt);
        u.setUpdatedAt(updatedAt);
        return u;
    }

    private static void setBaseId(User u, Long id) {
        try {
            java.lang.reflect.Field f = BaseEntity.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(u, id);
        } catch (Exception ignored) {}
    }
}
