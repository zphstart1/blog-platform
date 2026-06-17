package com.blog.article.infrastructure;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * Tag Mapper
 */
@Mapper
public interface TagMapper extends BaseMapper<TagPO> {

    @Select("SELECT * FROM tag WHERE slug = #{slug}")
    Optional<TagPO> findBySlug(String slug);

    @Select("SELECT COUNT(*) FROM tag WHERE name = #{name}" +
            " AND (#{excludeId} IS NULL OR id != #{excludeId})")
    int countByNameExclude(String name, Long excludeId);

    @Select("SELECT * FROM tag WHERE id IN (${ids})")
    List<TagPO> findByIds(String ids);
}
