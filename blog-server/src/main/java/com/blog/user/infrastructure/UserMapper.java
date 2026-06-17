package com.blog.user.infrastructure;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.Optional;

@Mapper
public interface UserMapper extends BaseMapper<UserPO> {
    @Select("SELECT * FROM user WHERE username = #{username}")
    Optional<UserPO> findByUsername(String username);

    @Select("SELECT * FROM user WHERE email = #{email}")
    Optional<UserPO> findByEmail(String email);
}
