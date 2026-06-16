package com.blog.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 作者简要信息 VO
 */
@Data
@NoArgsConstructor
public class AuthorVO {

    private Long id;
    private String nickname;
    private String avatar;
}