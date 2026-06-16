package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.ArticleVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ArticleVersionMapper extends BaseMapper<ArticleVersion> {

    /**
     * 获取文章当前最大版本号
     */
    @Select("SELECT MAX(version_no) FROM article_version WHERE article_id = #{articleId}")
    Integer selectMaxVersionNo(@Param("articleId") Long articleId);
}