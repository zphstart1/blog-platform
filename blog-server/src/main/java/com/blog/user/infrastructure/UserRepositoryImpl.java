package com.blog.user.infrastructure;

import com.blog.user.domain.*;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final UserMapper userMapper;

    public UserRepositoryImpl(UserMapper userMapper) { this.userMapper = userMapper; }

    @Override public User save(User user) {
        UserPO po = UserConverter.toPO(user);
        userMapper.insert(po);
        return UserConverter.toDomain(po);
    }

    @Override public Optional<User> findById(UserId id) {
        return Optional.ofNullable(userMapper.selectById(id.value())).map(UserConverter::toDomain);
    }

    @Override public Optional<User> findByUsername(Username username) {
        return userMapper.findByUsername(username.value()).map(UserConverter::toDomain);
    }

    @Override public Optional<User> findByEmail(Email email) {
        return userMapper.findByEmail(email.value()).map(UserConverter::toDomain);
    }

    @Override public boolean existsByUsername(Username username) {
        return userMapper.findByUsername(username.value()).isPresent();
    }

    @Override public boolean existsByEmail(Email email) {
        return userMapper.findByEmail(email.value()).isPresent();
    }
}
