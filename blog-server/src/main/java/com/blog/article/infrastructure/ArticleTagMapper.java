package com.blog.article.infrastructure;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Article-Tag 关联表 Mapper
 */
@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTagPO> {

    @Select("SELECT tag_id FROM article_tag WHERE article_id = #{articleId}")
    List<Long> findTagIdsByArticleId(@Param("articleId") Long articleId);

    @Insert("INSERT INTO article_tag (article_id, tag_id) VALUES (#{articleId}, #{tagId})")
    int insertIgnore(@Param("articleId") Long articleId, @Param("tagId") Long tagId);

    @Delete("DELETE FROM article_tag WHERE article_id = #{articleId}")
    int deleteByArticleId(@Param("articleId") Long articleId);

    @Select("SELECT COUNT(*) FROM article_tag WHERE tag_id = #{tagId}")
    int countByTagId(@Param("tagId") Long tagId);
}
