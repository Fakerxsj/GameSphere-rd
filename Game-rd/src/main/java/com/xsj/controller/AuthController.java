package com.xsj.controller;

import com.xsj.dto.request.LoginRequest;
import com.xsj.dto.request.RegisterRequest;
import com.xsj.dto.response.ApiResponse;
import com.xsj.entity.User;
import com.xsj.service.UserService;
import com.xsj.util.JwtUtil;
import com.xsj.util.MD5Util;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "用户认证", description = "登录注册相关接口")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ApiResponse<?> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.lambdaQuery()
                .eq(User::getUsername, request.getUsername())
                .one();

        if (user == null) {
            return ApiResponse.error(400, "用户名或密码错误");
        }

        if (!MD5Util.verify(request.getPassword(), user.getPassword())) {
            return ApiResponse.error(400, "用户名或密码错误");
        }

        if (user.getStatus() != 1) {
            return ApiResponse.error(403, "账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        user.setLastLoginTime(new Date());
        userService.updateById(user);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("avatar", user.getAvatar());

        return ApiResponse.success("登录成功", data);
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public ApiResponse<?> register(@Valid @RequestBody RegisterRequest request) {
        User existingUser = userService.lambdaQuery()
                .eq(User::getUsername, request.getUsername())
                .one();

        if (existingUser != null) {
            return ApiResponse.error(400, "用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(MD5Util.encrypt(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setRoleId(2L);
        user.setStatus(1);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        userService.save(user);

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("username", user.getUsername());

        return ApiResponse.success("注册成功", data);
    }

    @GetMapping("/logout")
    @Operation(summary = "用户登出")
    public ApiResponse<?> logout() {
        return ApiResponse.success("登出成功", null);
    }
}
