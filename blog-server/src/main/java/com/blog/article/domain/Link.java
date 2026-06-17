package com.blog.article.domain;

import com.blog.shared.BaseEntity;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 友链聚合根
 */
@Getter
public class Link extends BaseEntity {

    private String name;
    private String url;
    private String logo;
    private String description;
    private int status; // 0=待审核, 1=已通过
    private int sortOrder;

    protected Link() {}

    public static Link create(String name, String url, String logo, String description, int sortOrder) {
        Link link = new Link();
        link.name = name;
        link.url = url;
        link.logo = logo;
        link.description = description;
        link.status = 0; // 默认待审核
        link.sortOrder = sortOrder;
        link.setCreatedAt(LocalDateTime.now());
        return link;
    }

    public void approve() {
        this.status = 1;
    }

    public void update(String name, String url, String logo, String description, int sortOrder) {
        if (StringUtils.hasText(name)) this.name = name;
        if (StringUtils.hasText(url)) this.url = url;
        if (logo != null) this.logo = logo;
        if (description != null) this.description = description;
        this.sortOrder = sortOrder;
    }

    // Getters provided by Lombok @Getter
}
