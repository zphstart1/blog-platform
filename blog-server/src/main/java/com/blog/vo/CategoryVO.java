package com.blog.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 分类 VO — 列表展示用
 */
@Data
@NoArgsConstructor
public class CategoryVO {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private Integer articleCount;
    private List<CategoryVO> children;
}