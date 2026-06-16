package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 标签实体 — 对应 tag 表
 */
@Data
@Accessors(chain = true)
@TableName("tag")
public class Tag {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String slug;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}