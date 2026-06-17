package com.blog.comment.domain;

public enum CommentStatus {
    PENDING("待审核"),
    APPROVED("已通过"),
    REJECTED("已拒绝");

    private final String desc;
    CommentStatus(String desc) { this.desc = desc; }
    public String getCode() { return name(); }
    public static CommentStatus fromCode(String code) {
        try { return valueOf(code); } catch (Exception e) { return PENDING; }
    }
}
