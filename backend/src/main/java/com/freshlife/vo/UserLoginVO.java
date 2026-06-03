package com.freshlife.vo;

import lombok.Data;

@Data
public class UserLoginVO {

    private String token;

    private UserInfoVO userInfo;
}
