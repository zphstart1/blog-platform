package com.blog.article.infrastructure;

import com.blog.article.domain.Link;
import com.blog.article.domain.LinkRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * LinkRepository 实现
 */
@Repository
public class LinkRepositoryImpl implements LinkRepository {

    private final LinkMapper linkMapper;

    public LinkRepositoryImpl(LinkMapper linkMapper) {
        this.linkMapper = linkMapper;
    }

    @Override
    public Link save(Link link) {
        LinkPO po = ArticleConverter.toPO(link);
        linkMapper.insert(po);
        return ArticleConverter.toDomain(po);
    }

    @Override
    public Link update(Link link) {
        LinkPO po = ArticleConverter.toPO(link);
        linkMapper.updateById(po);
        return link;
    }

    @Override
    public Optional<Link> findById(Long id) {
        return Optional.ofNullable(linkMapper.selectById(id))
                .map(ArticleConverter::toDomain);
    }

    @Override
    public List<Link> findAll() {
        return linkMapper.selectList(null).stream()
                .map(ArticleConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Link> findAllApproved() {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<LinkPO> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(LinkPO::getStatus, 1);
        wrapper.orderByAsc(LinkPO::getSortOrder);
        return linkMapper.selectList(wrapper).stream()
                .map(ArticleConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        linkMapper.deleteById(id);
    }
}
