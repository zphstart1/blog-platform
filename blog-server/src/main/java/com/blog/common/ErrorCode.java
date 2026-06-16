package com.blog.common;

/**
 * 业务错误码常量定义
 */
public class ErrorCode {

    public static final int SUCCESS = 0;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int CONFLICT = 409;
    public static final int TOO_MANY_REQUESTS = 429;
    public static final int INTERNAL_ERROR = 500;

    // 业务细分码 (1000+)
    public static final int USERNAME_EXISTS = 1001;
    public static final int EMAIL_EXISTS = 1002;
    public static final int LOGIN_FAILED = 1003;
    public static final int ACCOUNT_DISABLED = 1004;
    public static final int LOGIN_LOCKED = 1005;
    public static final int SLUG_EXISTS = 1006;
    public static final int ARTICLE_NOT_FOUND = 1007;
    public static final int NO_EDIT_PERMISSION = 1008;
    public static final int CATEGORY_EXISTS = 1009;
    public static final int TAG_EXISTS = 1010;
    public static final int COMMENT_TOO_FREQUENT = 1011;
    public static final int INVALID_FILE_TYPE = 1012;
    public static final int FILE_TOO_LARGE = 1013;
}