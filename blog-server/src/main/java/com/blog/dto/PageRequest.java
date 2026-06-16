package com.blog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * 分页请求基类 — 所有分页接口继承此 DTO
 * BUG-007修复: 添加 @NoArgsConstructor 使子类可正常编译
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class PageRequest {

    private int page = 1;
    private int size = 10;

    public int getPage() {
        return Math.max(page, 1);
    }

    public int getSize() {
        return Math.min(Math.max(size, 1), 50);
    }
}