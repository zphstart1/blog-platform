package com.blog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 * 分页结果包装类 — 统一分页响应格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private List<T> records;
    private long total;
    private int page;
    private int size;
    private int pages;

    public static <T> PageResult<T> of(List<T> records, long total, int page, int size) {
        int pages = (int) Math.ceil((double) total / size);
        return new PageResult<>(records, total, page, size, pages);
    }
}