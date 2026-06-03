package com.freshlife.vo;

import lombok.Data;

@Data
public class UserInfoVO {

    private Long id;

    private String username;

    private String phone;

    private String nickname;

    private String avatarUrl;

    private Integer status;
}
