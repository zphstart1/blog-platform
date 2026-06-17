package com.blog.article.domain;

import com.blog.shared.DomainException;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;

public final class TagId {
    private final Long value;

    public TagId(Long value) {
        if (value == null || value <= 0) throw new DomainException("标签ID不能为空");
        this.value = value;
    }

    public static TagId of(Long value) { return new TagId(value); }

    @JsonValue
    public Long value() { return value; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TagId)) return false;
        return Objects.equals(value, ((TagId) o).value);
    }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return String.valueOf(value); }
}
