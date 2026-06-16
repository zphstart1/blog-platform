package com.blog.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 评论状态枚举
 */
@Getter
@AllArgsConstructor
public enum CommentStatus {

    PENDING("PENDING", "待审核"),
    APPROVED("APPROVED", "已通过"),
    REJECTED("REJECTED", "已拒绝");

    private final String code;
    private final String desc;
}