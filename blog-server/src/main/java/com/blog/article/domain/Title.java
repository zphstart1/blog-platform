package com.blog.article.domain;

import com.blog.shared.DomainException;
import org.springframework.util.StringUtils;
import java.util.Objects;

public final class Title {
    private static final int MAX_LENGTH = 200;
    private final String value;

    public Title(String value) {
        if (!StringUtils.hasText(value)) throw new DomainException("文章标题不能为空");
        if (value.length() > MAX_LENGTH) throw new DomainException("标题不能超过" + MAX_LENGTH + "字符");
        this.value = value;
    }

    public static Title of(String value) { return new Title(value); }
    public String value() { return value; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Title)) return false;
        return Objects.equals(value, ((Title) o).value);
    }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return value; }
}
