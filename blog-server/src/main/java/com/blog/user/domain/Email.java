package com.blog.user.domain;

import com.blog.shared.DomainException;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;
import java.util.regex.Pattern;

public final class Email {
    private static final Pattern PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    private final String value;

    public Email(String value) {
        if (value == null || value.isBlank()) throw new DomainException("邮箱不能为空");
        if (!PATTERN.matcher(value).matches()) throw new DomainException("邮箱格式不正确");
        this.value = value;
    }

    public static Email of(String v) { return new Email(v); }

    @JsonValue
    public String value() { return value; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;
        return Objects.equals(value, ((Email) o).value);
    }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return value; }
}
