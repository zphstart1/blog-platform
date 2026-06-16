package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.entity.Comment;
import com.blog.vo.CommentVO;
import com.blog.vo.PendingCommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 文章评论列表 — 只查 APPROVED 状态，支持嵌套结构
     */
    IPage<CommentVO> selectApprovedCommentPage(IPage<?> page,
                                                 @Param("articleId") Long articleId,
                                                 @Param("sort") String sort);

    /**
     * 待审核评论列表 — 审核队列使用
     */
    IPage<PendingCommentVO> selectPendingCommentPage(IPage<?> page);
}