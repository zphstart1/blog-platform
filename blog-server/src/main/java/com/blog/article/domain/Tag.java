package com.blog.article.domain;

import com.blog.shared.BaseEntity;
import com.blog.shared.DomainException;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 标签聚合根
 */
@Getter
public class Tag extends BaseEntity {

    private String name;
    private Slug slug;

    protected Tag() {}

    public static Tag create(String name, Slug slug) {
        if (!StringUtils.hasText(name)) {
            throw new DomainException("标签名称不能为空");
        }

        Tag tag = new Tag();
        tag.name = name;
        tag.slug = slug;
        tag.setCreatedAt(LocalDateTime.now());
        return tag;
    }

    public void rename(String newName) {
        if (!StringUtils.hasText(newName)) {
            throw new DomainException("标签名称不能为空");
        }
        this.name = newName;
    }

    public void changeSlug(Slug newSlug) {
        this.slug = newSlug;
    }

    // Getters provided by Lombok @Getter
}
