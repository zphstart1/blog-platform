package com.blog.article.domain;

import java.util.Objects;

public final class ViewCount {
    private final int value;

    public ViewCount(int value) { this.value = Math.max(value, 0); }

    public static ViewCount zero() { return new ViewCount(0); }
    public static ViewCount of(int value) { return new ViewCount(value); }
    public int value() { return value; }

    public ViewCount increment() { return new ViewCount(this.value + 1); }
    public ViewCount increment(int delta) { return new ViewCount(this.value + delta); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ViewCount)) return false;
        return value == ((ViewCount) o).value;
    }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return String.valueOf(value); }
}
