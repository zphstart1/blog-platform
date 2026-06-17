package com.blog.shared;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 基础实体 — 所有聚合根和实体的父类
 */
@Getter
public abstract class BaseEntity {
    private Long id;
    @Setter
    private LocalDateTime createdAt;
    @Setter
    private LocalDateTime updatedAt;
}
