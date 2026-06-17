package com.blog.article.infrastructure;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * ArticleVersion Mapper
 */
@Mapper
public interface ArticleVersionMapper extends BaseMapper<ArticleVersionPO> {

    @Select("SELECT MAX(version_no) FROM article_version WHERE article_id = #{articleId}")
    Integer findMaxVersionNo(Long articleId);
}
