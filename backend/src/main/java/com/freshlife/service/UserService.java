package com.freshlife.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.freshlife.dto.UserLoginDTO;
import com.freshlife.dto.UserRegisterDTO;
import com.freshlife.entity.User;
import com.freshlife.vo.UserInfoVO;
import com.freshlife.vo.UserLoginVO;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService extends IService<User> {

    UserInfoVO register(UserRegisterDTO registerDTO);

    UserLoginVO login(UserLoginDTO loginDTO);

    UserInfoVO getUserInfo(HttpServletRequest request);
}
