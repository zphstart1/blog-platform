package com.blog.comment.infrastructure;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.vo.CommentVO;
import com.blog.vo.PendingCommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<CommentPO> {

    /** 已审核评论分页 */
    List<CommentVO> selectApprovedCommentPage(Page<CommentVO> page,
                                               @Param("articleId") Long articleId,
                                               @Param("sort") String sort);

    /** 待审核评论分页 */
    List<PendingCommentVO> selectPendingCommentPage(Page<PendingCommentVO> page);
}
