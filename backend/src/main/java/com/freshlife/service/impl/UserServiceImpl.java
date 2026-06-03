package com.freshlife.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshlife.dto.UserLoginDTO;
import com.freshlife.dto.UserRegisterDTO;
import com.freshlife.entity.User;
import com.freshlife.exception.BusinessException;
import com.freshlife.mapper.UserMapper;
import com.freshlife.service.UserService;
import com.freshlife.utils.JwtUtils;
import com.freshlife.utils.JwtUserUtils;
import com.freshlife.vo.UserInfoVO;
import com.freshlife.vo.UserLoginVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final int USER_STATUS_NORMAL = 1;

    private final JwtUtils jwtUtils;
    private final JwtUserUtils jwtUserUtils;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(JwtUtils jwtUtils, JwtUserUtils jwtUserUtils) {
        this.jwtUtils = jwtUtils;
        this.jwtUserUtils = jwtUserUtils;
    }

    @Override
    public UserInfoVO register(UserRegisterDTO registerDTO) {
        User existedUser = getByUsername(registerDTO.getUsername());
        if (existedUser != null) {
            throw new BusinessException(409, "用户名已存在");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setPhone(registerDTO.getPhone());
        user.setNickname(registerDTO.getUsername());
        user.setStatus(USER_STATUS_NORMAL);
        user.setDeleted(0);
        save(user);

        return toUserInfoVO(user);
    }

    @Override
    public UserLoginVO login(UserLoginDTO loginDTO) {
        User user = getByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "密码错误");
        }

        String token = jwtUtils.generateToken(
                String.valueOf(user.getId()),
                Map.of("username", user.getUsername())
        );

        UserLoginVO loginVO = new UserLoginVO();
        loginVO.setToken(token);
        loginVO.setUserInfo(toUserInfoVO(user));
        return loginVO;
    }

    @Override
    public UserInfoVO getUserInfo(HttpServletRequest request) {
        Long userId = jwtUserUtils.getCurrentUserId(request);
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return toUserInfoVO(user);
    }

    private User getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .last("LIMIT 1"));
    }

    private UserInfoVO toUserInfoVO(User user) {
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setId(user.getId());
        userInfoVO.setUsername(user.getUsername());
        userInfoVO.setPhone(user.getPhone());
        userInfoVO.setNickname(user.getNickname());
        userInfoVO.setAvatarUrl(user.getAvatarUrl());
        userInfoVO.setStatus(user.getStatus());
        return userInfoVO;
    }
}
