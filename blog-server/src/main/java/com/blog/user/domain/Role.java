package com.blog.user.domain;

public enum Role {
    OWNER, ADMIN, AUTHOR, VISITOR;

    public static Role fromString(String role) {
        try { return valueOf(role != null ? role.toUpperCase() : "VISITOR"); }
        catch (IllegalArgumentException e) { return VISITOR; }
    }

    public boolean isOwner() { return this == OWNER; }
    public boolean canManageUsers() { return this == OWNER || this == ADMIN; }
}
