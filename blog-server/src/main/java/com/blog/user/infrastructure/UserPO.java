package com.blog.user.infrastructure;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

@Data @Accessors(chain = true) @TableName("user")
public class UserPO {
    @TableId(type = IdType.AUTO) private Long id;
    private String username;
    private String password;
    private String email;
    private String nickname;
    private String avatar;
    private String role;
    private Integer status;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
}
