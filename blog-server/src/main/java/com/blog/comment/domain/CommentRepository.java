package com.blog.comment.domain;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Comment save(Comment comment);
    Optional<Comment> findById(CommentId id);
    List<Comment> findByArticleId(Long articleId, int page, int size);
    long countByArticleId(Long articleId);
    List<Comment> findPending(int page, int size);
    long countPending();
    void delete(CommentId id);
}
