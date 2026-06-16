package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 统计分类下的文章数量
     */
    @Select("SELECT COUNT(*) FROM article WHERE category_id = #{categoryId} AND status = 'PUBLISHED'")
    int countArticlesByCategoryId(@Param("categoryId") Long categoryId);
}