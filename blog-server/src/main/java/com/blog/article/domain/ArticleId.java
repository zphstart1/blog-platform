package com.blog.article.domain;

import com.blog.shared.DomainException;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;

/**
 * 文章ID值对象
 */
public final class ArticleId {
    private final Long value;

    public ArticleId(Long value) {
        if (value == null || value <= 0) throw new DomainException("文章ID不能为空或无效");
        this.value = value;
    }

    public static ArticleId of(Long value) { return new ArticleId(value); }

    @JsonValue
    public Long value() { return value; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleId)) return false;
        return Objects.equals(value, ((ArticleId) o).value);
    }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return String.valueOf(value); }
}
