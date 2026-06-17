package com.blog.article.domain;

import com.blog.shared.DomainException;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;

public final class CategoryId {
    private final Long value;

    public CategoryId(Long value) {
        if (value == null || value <= 0) throw new DomainException("分类ID不能为空");
        this.value = value;
    }

    public static CategoryId of(Long value) { return new CategoryId(value); }

    @JsonValue
    public Long value() { return value; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryId)) return false;
        return Objects.equals(value, ((CategoryId) o).value);
    }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return String.valueOf(value); }
}
