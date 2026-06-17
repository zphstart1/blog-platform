package com.blog.article.infrastructure;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * Tag 持久化对象 — 对应 tag 表
 */
@Data
@Accessors(chain = true)
@TableName("tag")
public class TagPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String slug;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
