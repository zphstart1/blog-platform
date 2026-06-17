package com.blog.article.domain;

import com.blog.shared.BaseEntity;
import com.blog.shared.DomainException;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 分类聚合根
 */
@Getter
public class Category extends BaseEntity {

    private String name;
    private Slug slug;
    private String description;
    private CategoryId parentId;
    private int sortOrder;

    protected Category() {}

    public static Category create(String name, Slug slug, String description, CategoryId parentId, int sortOrder) {
        if (!StringUtils.hasText(name)) {
            throw new DomainException("分类名称不能为空");
        }

        Category category = new Category();
        category.name = name;
        category.slug = slug;
        category.description = description;
        category.parentId = parentId;
        category.sortOrder = sortOrder;
        category.setCreatedAt(LocalDateTime.now());
        return category;
    }

    public void rename(String newName) {
        if (!StringUtils.hasText(newName)) {
            throw new DomainException("分类名称不能为空");
        }
        this.name = newName;
    }

    public void changeSlug(Slug newSlug) {
        this.slug = newSlug;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changeSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    // Getters provided by Lombok @Getter
}
