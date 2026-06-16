package com.blog.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审核操作枚举
 */
@Getter
@AllArgsConstructor
public enum ReviewAction {

    APPROVE("APPROVE", "通过"),
    REJECT("REJECT", "拒绝");

    private final String code;
    private final String desc;
}