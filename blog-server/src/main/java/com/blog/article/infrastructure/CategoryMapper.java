package com.blog.article.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * Category Mapper
 */
@Mapper
public interface CategoryMapper extends BaseMapper<CategoryPO> {

    @Select("SELECT * FROM category WHERE slug = #{slug}")
    Optional<CategoryPO> findBySlug(String slug);

    @Select("SELECT COUNT(*) FROM category WHERE name = #{name}" +
            " AND (#{excludeId} IS NULL OR id != #{excludeId})")
    int countByNameExclude(String name, Long excludeId);

    default List<CategoryPO> findAllOrdered() {
        LambdaQueryWrapper<CategoryPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(CategoryPO::getSortOrder);
        return selectList(wrapper);
    }
}
