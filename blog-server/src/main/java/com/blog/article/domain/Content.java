package com.blog.article.domain;

import org.springframework.util.StringUtils;
import java.util.Objects;

public final class Content {
    private final String value;

    public Content(String value) { this.value = value == null ? "" : value; }

    public static Content of(String value) { return new Content(value); }
    public String value() { return value; }
    public boolean isEmpty() { return !StringUtils.hasText(value); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Content)) return false;
        return Objects.equals(value, ((Content) o).value);
    }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return value; }
}
