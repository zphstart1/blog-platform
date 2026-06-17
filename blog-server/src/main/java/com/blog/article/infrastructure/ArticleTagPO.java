package com.blog.article.infrastructure;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Article-Tag 关联表持久化对象 — 对应 article_tag 表
 */
@Data
@Accessors(chain = true)
@TableName("article_tag")
public class ArticleTagPO {

    private Long articleId;

    private Long tagId;
}
