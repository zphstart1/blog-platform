package com.blog.shared;

/**
 * 领域异常 — Domain 层抛出的业务错误
 */
public class DomainException extends RuntimeException {
    private final int code;

    public DomainException(String message) {
        super(message);
        this.code = 400;
    }

    public DomainException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() { return code; }
}
