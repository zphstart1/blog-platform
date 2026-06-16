package com.blog.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标签 VO — 列表展示用
 */
@Data
@NoArgsConstructor
public class TagVO {

    private Long id;
    private String name;
    private String slug;
    private Integer articleCount;
    /** 标签云权重（文章数） */
    private Integer weight;
}