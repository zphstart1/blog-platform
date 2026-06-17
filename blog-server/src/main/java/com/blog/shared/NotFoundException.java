package com.blog.shared;

/**
 * 资源未找到异常
 */
public class NotFoundException extends DomainException {
    public NotFoundException(String message) {
        super(404, message);
    }
}
