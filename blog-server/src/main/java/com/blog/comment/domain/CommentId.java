package com.blog.comment.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;

public final class CommentId {
    private final Long value;

    public CommentId(Long value) {
        if (value == null || value <= 0) throw new IllegalArgumentException("评论ID不能为空");
        this.value = value;
    }

    public static CommentId of(Long v) { return new CommentId(v); }

    @JsonValue
    public Long value() { return value; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommentId)) return false;
        return Objects.equals(value, ((CommentId) o).value);
    }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return String.valueOf(value); }
}
