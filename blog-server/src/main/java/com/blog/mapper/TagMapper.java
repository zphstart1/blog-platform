package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 统计标签下的文章数量
     */
    @Select("SELECT COUNT(*) FROM article_tag WHERE tag_id = #{tagId}")
    int countArticlesByTagId(@Param("tagId") Long tagId);
}