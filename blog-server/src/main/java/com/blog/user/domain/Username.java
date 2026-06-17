package com.blog.user.domain;

import com.blog.shared.DomainException;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;

public final class Username {
    private final String value;

    public Username(String value) {
        if (value == null || value.isBlank()) throw new DomainException("用户名不能为空");
        if (value.length() < 3 || value.length() > 50) throw new DomainException("用户名长度3-50字符");
        this.value = value;
    }

    public static Username of(String v) { return new Username(v); }

    @JsonValue
    public String value() { return value; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Username)) return false;
        return Objects.equals(value, ((Username) o).value);
    }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return value; }
}
