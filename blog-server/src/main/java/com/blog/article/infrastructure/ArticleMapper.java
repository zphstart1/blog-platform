package com.blog.article.infrastructure;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.vo.ArticleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

/**
 * Article Mapper — MyBatis-Plus BaseMapper + 自定义查询
 */
@Mapper
public interface ArticleMapper extends BaseMapper<ArticlePO> {

    /** 按 slug 查 */
    @Select("SELECT * FROM article WHERE slug = #{slug}")
    Optional<ArticlePO> findBySlug(@Param("slug") String slug);

    /** 检查 slug 是否存在（排除指定文章） */
    @Select("SELECT COUNT(*) FROM article WHERE slug = #{slug}" +
            " AND (#{excludeId} IS NULL OR id != #{excludeId})")
    int countBySlugExclude(@Param("slug") String slug, @Param("excludeId") Long excludeId);

    /** 查询前一篇已发布文章 */
    @Select("SELECT * FROM article WHERE status = 'PUBLISHED' AND id < #{id}" +
            " ORDER BY id DESC LIMIT 1")
    Optional<ArticlePO> findPrevPublished(@Param("id") Long id);

    /** 查询后一篇已发布文章 */
    @Select("SELECT * FROM article WHERE status = 'PUBLISHED' AND id > #{id}" +
            " ORDER BY id ASC LIMIT 1")
    Optional<ArticlePO> findNextPublished(@Param("id") Long id);

    /** 批量更新阅读量 */
    @Update("UPDATE article SET view_count = view_count + #{delta} WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id, @Param("delta") int delta);

    // ========== XML 复杂查询（join category/tag/user） ==========

    /** 公开文章分页列表 */
    List<ArticleVO> selectPublicArticlePage(Page<ArticleVO> page,
                                            @Param("categoryId") Long categoryId,
                                            @Param("tagId") Long tagId,
                                            @Param("keyword") String keyword,
                                            @Param("sortField") String sortField,
                                            @Param("sortOrder") String sortOrder);

    /** 按 slug 查文章详情（含分类/标签/作者） */
    List<ArticleVO> selectArticleDetailBySlug(@Param("slug") String slug);

    /** 全文搜索 */
    List<ArticleVO> selectSearchPage(Page<ArticleVO> page,
                                     @Param("keyword") String keyword);

    /** 管理端文章分页列表 */
    List<ArticleVO> selectAdminArticlePage(Page<ArticleVO> page,
                                           @Param("status") String status,
                                           @Param("categoryId") Long categoryId,
                                           @Param("authorId") Long authorId);

    /** 管理端按 ID 查文章详情 */
    ArticleVO selectAdminArticleById(@Param("id") Long id);
}
