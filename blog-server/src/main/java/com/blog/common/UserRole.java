package com.blog.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
@AllArgsConstructor
public enum UserRole {

    OWNER("OWNER", "博主"),
    ADMIN("ADMIN", "管理员"),
    AUTHOR("AUTHOR", "作者"),
    VISITOR("VISITOR", "访客");

    private final String code;
    private final String desc;

    /**
     * 判断角色是否拥有管理权限（OWNER/ADMIN/AUTHOR）
     */
    public boolean isManager() {
        return this == OWNER || this == ADMIN || this == AUTHOR;
    }

    /**
     * 判断角色是否拥有审核权限（OWNER/ADMIN）
     */
    public boolean canReview() {
        return this == OWNER || this == ADMIN;
    }
}