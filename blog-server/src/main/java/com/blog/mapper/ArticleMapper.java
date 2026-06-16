package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.entity.Article;
import com.blog.vo.ArticleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 公开文章列表 — 只查 PUBLISHED 状态，支持分类/标签筛选
     */
    IPage<ArticleVO> selectPublicArticlePage(IPage<?> page,
                                               @Param("categoryId") Long categoryId,
                                               @Param("tagId") Long tagId,
                                               @Param("keyword") String keyword,
                                               @Param("sortField") String sortField,
                                               @Param("sortOrder") String sortOrder);

    /**
     * 管理端文章列表 — 包含 DRAFT 状态
     */
    IPage<ArticleVO> selectAdminArticlePage(IPage<?> page,
                                              @Param("status") String status,
                                              @Param("categoryId") Long categoryId,
                                              @Param("authorId") Long authorId);

    /**
     * 文章详情 — 按 slug 查询
     */
    ArticleVO selectArticleDetailBySlug(@Param("slug") String slug);

    /**
     * 全文搜索 — MySQL FULLTEXT MATCH AGAINST
     */
    IPage<ArticleVO> selectSearchPage(IPage<?> page, @Param("keyword") String keyword);

    /**
     * 管理端按ID查文章详情 — 包含所有状态（DRAFT/PUBLISHED），用于编辑回填
     */
    ArticleVO selectAdminArticleById(@Param("id") Long id);

    /**
     * 直接SQL更新阅读量 — 绕过MyBatis-Plus null字段策略
     * BUG-003修复: view_count = view_count + increment, 不依赖 updateById(null字段不更新)
     */
    @Update("UPDATE article SET view_count = view_count + #{increment} WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id, @Param("increment") int increment);
}