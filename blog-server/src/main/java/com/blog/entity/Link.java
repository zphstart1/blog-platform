package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 友链实体 — 对应 link 表 (P1)
 */
@Data
@Accessors(chain = true)
@TableName("link")
public class Link {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String url;

    private String logo;

    private String description;

    private Integer status;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}