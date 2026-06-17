package com.blog.comment.domain;

import com.blog.shared.DomainException;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;

public final class CommentContent {
    private final String value;

    public CommentContent(String value) {
        if (value == null || value.isBlank()) throw new DomainException("评论内容不能为空");
        if (value.length() > 2000) throw new DomainException("评论内容不能超过2000字符");
        this.value = value;
    }

    public static CommentContent of(String v) { return new CommentContent(v); }
    @JsonValue
    public String value() { return value; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommentContent)) return false;
        return Objects.equals(value, ((CommentContent) o).value);
    }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return value; }
}
