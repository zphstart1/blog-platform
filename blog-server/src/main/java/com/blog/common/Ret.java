package com.blog.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * 统一返回包装类，所有接口返回值均使用此格式
 * 不抛异常，通过 code/message 表达业务状态
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Ret<T> {

    private int code;
    private String message;
    private T data;

    public static <T> Ret<T> ok() {
        return new Ret<>(0, "success", null);
    }

    public static <T> Ret<T> ok(T data) {
        return new Ret<>(0, "success", data);
    }

    public static <T> Ret<T> ok(String message, T data) {
        return new Ret<>(0, message, data);
    }

    public static <T> Ret<T> fail(int code, String message) {
        return new Ret<>(code, message, null);
    }

    public static <T> Ret<T> fail(String message) {
        return new Ret<>(500, message, null);
    }

    public static <T> Ret<T> badRequest(String message) {
        return new Ret<>(400, message, null);
    }

    public static <T> Ret<T> unauthorized(String message) {
        return new Ret<>(401, message, null);
    }

    public static <T> Ret<T> forbidden(String message) {
        return new Ret<>(403, message, null);
    }

    public static <T> Ret<T> notFound(String message) {
        return new Ret<>(404, message, null);
    }

    public static <T> Ret<T> conflict(String message) {
        return new Ret<>(409, message, null);
    }

    public static <T> Ret<T> tooManyRequests(String message) {
        return new Ret<>(429, message, null);
    }

    public boolean isSuccess() {
        return this.code == 0;
    }
}