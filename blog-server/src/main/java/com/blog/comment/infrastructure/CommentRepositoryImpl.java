package com.blog.comment.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.comment.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CommentRepositoryImpl implements CommentRepository {
    private final CommentMapper mapper;

    public CommentRepositoryImpl(CommentMapper mapper) { this.mapper = mapper; }

    @Override public Comment save(Comment c) {
        CommentPO po = CommentConverter.toPO(c);
        mapper.insert(po);
        return c;
    }

    @Override public Optional<Comment> findById(CommentId id) {
        return Optional.ofNullable(mapper.selectById(id.value())).map(CommentConverter::toDomain);
    }

    @Override public List<Comment> findByArticleId(Long articleId, int page, int size) {
        LambdaQueryWrapper<CommentPO> w = new LambdaQueryWrapper<>();
        w.eq(CommentPO::getArticleId, articleId);
        w.eq(CommentPO::getStatus, CommentStatus.APPROVED.getCode());
        w.orderByAsc(CommentPO::getCreatedAt);
        Page<CommentPO> p = new Page<>(page, size);
        return mapper.selectPage(p, w).getRecords().stream().map(CommentConverter::toDomain).collect(Collectors.toList());
    }

    @Override public long countByArticleId(Long articleId) {
        LambdaQueryWrapper<CommentPO> w = new LambdaQueryWrapper<>();
        w.eq(CommentPO::getArticleId, articleId);
        w.eq(CommentPO::getStatus, CommentStatus.APPROVED.getCode());
        return mapper.selectCount(w);
    }

    @Override public List<Comment> findPending(int page, int size) {
        LambdaQueryWrapper<CommentPO> w = new LambdaQueryWrapper<>();
        w.eq(CommentPO::getStatus, CommentStatus.PENDING.getCode());
        w.orderByDesc(CommentPO::getCreatedAt);
        Page<CommentPO> p = new Page<>(page, size);
        return mapper.selectPage(p, w).getRecords().stream().map(CommentConverter::toDomain).collect(Collectors.toList());
    }

    @Override public long countPending() {
        LambdaQueryWrapper<CommentPO> w = new LambdaQueryWrapper<>();
        w.eq(CommentPO::getStatus, CommentStatus.PENDING.getCode());
        return mapper.selectCount(w);
    }

    @Override public void delete(CommentId id) {
        mapper.deleteById(id.value());
    }
}
