package com.blog.article.domain;

import java.util.List;
import java.util.Optional;

/**
 * Link 仓储接口
 */
public interface LinkRepository {

    Link save(Link link);

    Link update(Link link);

    Optional<Link> findById(Long id);

    List<Link> findAll();

    List<Link> findAllApproved();

    void delete(Long id);
}
