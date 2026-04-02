package com.xsj.controller;

import com.xsj.dto.response.ApiResponse;
import com.xsj.dto.response.PageResponse;
import com.xsj.dto.response.UserResponse;
import com.xsj.entity.User;
import com.xsj.service.UserService;
import com.xsj.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户信息相关接口")
public class UserController {

    private final UserService userService;

    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息")
    public ApiResponse<?> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        User user = userService.getById(userId);
        if (user == null) {
            return ApiResponse.error(404, "用户不存在");
        }

        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);

        return ApiResponse.success(response);
    }

    @PutMapping("/info")
    @Operation(summary = "更新用户信息")
    public ApiResponse<?> updateUserInfo(HttpServletRequest request, @RequestBody User user) {
        Long userId = (Long) request.getAttribute("userId");

        User existingUser = userService.getById(userId);
        if (existingUser == null) {
            return ApiResponse.error(404, "用户不存在");
        }

        if (user.getNickname() != null) {
            existingUser.setNickname(user.getNickname());
        }
        if (user.getSignature() != null) {
            existingUser.setSignature(user.getSignature());
        }
        if (user.getAvatar() != null) {
            existingUser.setAvatar(user.getAvatar());
        }
        if (user.getGender() != null) {
            existingUser.setGender(user.getGender());
        }
        if (user.getBirthday() != null) {
            existingUser.setBirthday(user.getBirthday());
        }

        existingUser.setUpdateTime(new java.util.Date());
        userService.updateById(existingUser);

        return ApiResponse.success("更新成功", null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取指定用户信息")
    public ApiResponse<?> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return ApiResponse.error(404, "用户不存在");
        }

        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setSignature(user.getSignature());

        return ApiResponse.success(vo);
    }
}
