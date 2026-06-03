package com.freshlife.controller;

import com.freshlife.common.Result;
import com.freshlife.dto.UserLoginDTO;
import com.freshlife.dto.UserRegisterDTO;
import com.freshlife.service.UserService;
import com.freshlife.vo.UserInfoVO;
import com.freshlife.vo.UserLoginVO;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<UserInfoVO> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        return Result.success(userService.register(registerDTO));
    }

    @PostMapping("/login")
    public Result<UserLoginVO> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        return Result.success(userService.login(loginDTO));
    }

    @GetMapping("/info")
    public Result<UserInfoVO> info(HttpServletRequest request) {
        return Result.success(userService.getUserInfo(request));
    }
}
