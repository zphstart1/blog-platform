package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 文章-标签关联实体 — 对应 article_tag 表
 */
@Data
@Accessors(chain = true)
@TableName("article_tag")
public class ArticleTag {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long articleId;

    private Long tagId;
}