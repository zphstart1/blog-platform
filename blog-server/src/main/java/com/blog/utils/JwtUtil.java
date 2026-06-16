package com.blog.utils;

import com.blog.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

/**
 * JWT 工具类 — Token 签发/解析/校验
 * 使用 HS256 算法，Key 从配置文件读取
 */
@Slf4j
@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private Key signingKey;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    public void init() {
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    /**
     * 签发 JWT Token
     */
    public String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getExpiration());

        String token = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();

        log.info("签发Token: userId={}, username={}, role={}", userId, username, role);
        return token;
    }

    /**
     * 解析 JWT Token，返回 Claims；失败返回 null
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.warn("Token解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从 Token 中提取用户 ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 从 Token 中提取用户角色
     */
    public String getRole(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        return claims.get("role", String.class);
    }

    /**
     * 校验 Token 是否有效
     */
    public boolean validateToken(String token) {
        return parseToken(token) != null;
    }
}