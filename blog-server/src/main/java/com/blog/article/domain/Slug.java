package com.blog.article.domain;

import com.blog.shared.DomainException;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;

public final class Slug {
    private final String value;

    public Slug(String value) {
        if (value == null || value.isBlank()) throw new DomainException("slug 不能为空");
        if (value.length() > 100) throw new DomainException("slug 不能超过100个字符");
        this.value = value;
    }

    public static Slug of(String value) { return new Slug(value); }

    @JsonValue
    public String value() { return value; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Slug)) return false;
        return Objects.equals(value, ((Slug) o).value);
    }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return value; }
}
