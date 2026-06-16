package com.blog.config;

import com.blog.common.Ret;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器 — 不抛异常给前端，统一包装为 Ret 格式返回
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public Ret<Void> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", msg);
        return Ret.badRequest(msg);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.OK)
    public Ret<Void> handleBindException(BindException e) {
        String msg = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数绑定失败: {}", msg);
        return Ret.badRequest(msg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    public Ret<Void> handleConstraint(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.warn("约束校验失败: {}", msg);
        return Ret.badRequest(msg);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.OK)
    public Ret<Void> handleMaxUploadSize(MaxUploadSizeExceededException e) {
        log.warn("文件大小超限: {}", e.getMessage());
        return Ret.badRequest("文件大小不能超过5MB");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public Ret<Void> handleException(Exception e) {
        log.error("未预期异常: ", e);
        return Ret.fail("服务器内部错误");
    }
}