package com.blog.comment.infrastructure;

import com.blog.comment.domain.*;

public final class CommentConverter {
    private CommentConverter() {}

    public static CommentPO toPO(Comment d) {
        CommentPO po = new CommentPO();
        if (d.getId() != null) po.setId(d.getId());
        po.setArticleId(d.getArticleId());
        po.setUserId(d.getUserId());
        po.setParentId(d.getParentId());
        po.setReplyToId(d.getReplyToId());
        po.setAuthorName(d.getAuthorName());
        po.setAuthorEmail(d.getAuthorEmail());
        po.setAuthorWebsite(d.getAuthorWebsite());
        po.setContent(d.getContent().value());
        po.setStatus(d.getStatus().getCode());
        po.setUserAgent(d.getUserAgent());
        po.setIp(d.getIp());
        po.setCreatedAt(d.getCreatedAt());
        return po;
    }

    public static Comment toDomain(CommentPO po) {
        if (po == null) return null;
        return Comment.reconstruct(
                po.getId(), po.getArticleId(), po.getUserId(),
                po.getParentId(), po.getReplyToId(),
                po.getAuthorName(), po.getAuthorEmail(), po.getAuthorWebsite(),
                CommentContent.of(po.getContent()),
                CommentStatus.fromCode(po.getStatus()),
                po.getUserAgent(), po.getIp(),
                po.getCreatedAt()
        );
    }
}
