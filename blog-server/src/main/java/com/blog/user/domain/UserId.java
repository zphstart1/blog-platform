package com.blog.user.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;

public final class UserId {
    private final Long value;

    public UserId(Long value) {
        if (value == null || value <= 0) throw new IllegalArgumentException("用户ID不能为空");
        this.value = value;
    }

    public static UserId of(Long v) { return new UserId(v); }

    @JsonValue
    public Long value() { return value; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId)) return false;
        return Objects.equals(value, ((UserId) o).value);
    }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return String.valueOf(value); }
}
