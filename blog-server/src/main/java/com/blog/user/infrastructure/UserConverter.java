package com.blog.user.infrastructure;

import com.blog.user.domain.*;

public final class UserConverter {
    private UserConverter() {}

    public static UserPO toPO(User domain) {
        UserPO po = new UserPO();
        if (domain.getId() != null) po.setId(domain.getId());
        po.setUsername(domain.getUsername().value());
        po.setPassword(domain.getPassword());
        po.setEmail(domain.getEmail().value());
        po.setNickname(domain.getNickname());
        po.setAvatar(domain.getAvatar());
        po.setRole(domain.getRole().name());
        po.setStatus(domain.getStatus());
        po.setCreatedAt(domain.getCreatedAt());
        po.setUpdatedAt(domain.getUpdatedAt());
        return po;
    }

    public static User toDomain(UserPO po) {
        if (po == null) return null;
        return User.reconstruct(
                po.getId(),
                Username.of(po.getUsername()),
                po.getPassword(),
                Email.of(po.getEmail()),
                po.getNickname(),
                po.getAvatar(),
                Role.fromString(po.getRole()),
                po.getStatus() != null ? po.getStatus() : 1,
                po.getCreatedAt(),
                po.getUpdatedAt()
        );
    }
}
