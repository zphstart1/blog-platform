package com.blog.user.domain;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UserId id);
    Optional<User> findByUsername(Username username);
    Optional<User> findByEmail(Email email);
    boolean existsByUsername(Username username);
    boolean existsByEmail(Email email);
}
