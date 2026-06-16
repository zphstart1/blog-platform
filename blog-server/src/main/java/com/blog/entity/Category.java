package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 分类实体 — 对应 category 表
 */
@Data
@Accessors(chain = true)
@TableName("category")
public class Category {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String slug;

    private String description;

    private Long parentId;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}