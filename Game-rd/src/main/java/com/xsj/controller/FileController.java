package com.xsj.controller;

import com.xsj.dto.response.ApiResponse;
import com.xsj.entity.User;
import com.xsj.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@Tag(name = "文件上传", description = "头像等文件上传接口")
public class FileController {

    private final UserService userService;

    @Value("${file.upload.avatar-path}")
    private String avatarPath;

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${server.port}")
    private String serverPort;

    @PostMapping("/avatar")
    @Operation(summary = "上传用户头像")
    public ApiResponse<?> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        if (file.isEmpty()) {
            return ApiResponse.error(400, "请选择文件");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return ApiResponse.error(400, "文件名无效");
        }

        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!suffix.matches("\\.(jpg|jpeg|png|gif|webp|bmp)$")) {
            return ApiResponse.error(400, "仅支持 jpg、png、gif、webp、bmp 格式");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            return ApiResponse.error(400, "文件大小不能超过 5MB");
        }

        try {
            File dir = new File(avatarPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = "avatar_" + userId + "_" + UUID.randomUUID().toString().replace("-", "") + suffix;
            File dest = new File(dir, fileName);
            file.transferTo(dest);

            String avatarUrl = "http://localhost:" + serverPort + "/api/file/avatar/" + fileName;

            User user = userService.getById(userId);
            if (user != null) {
                user.setAvatar(avatarUrl);
                user.setUpdateTime(new Date());
                userService.updateById(user);
            }

            return ApiResponse.success("上传成功", avatarUrl);
        } catch (IOException e) {
            log.error("头像上传失败", e);
            return ApiResponse.error(500, "上传失败：" + e.getMessage());
        }
    }

    @PostMapping("/chat-image")
    @Operation(summary = "上传聊天图片")
    public ApiResponse<?> uploadChatImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        if (file.isEmpty()) {
            return ApiResponse.error(400, "请选择文件");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return ApiResponse.error(400, "文件名无效");
        }

        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!suffix.matches("\\.(jpg|jpeg|png|gif|webp|bmp)$")) {
            return ApiResponse.error(400, "仅支持 jpg、png、gif、webp、bmp 格式");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            return ApiResponse.error(400, "文件大小不能超过 10MB");
        }

        try {
            String chatImagePath = uploadPath + "chat/";
            File dir = new File(chatImagePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String dateStr = new java.text.SimpleDateFormat("yyyyMMdd").format(new Date());
            String fileName = "chat_" + userId + "_" + dateStr + "_" + UUID.randomUUID().toString().replace("-", "") + suffix;
            File dest = new File(dir, fileName);
            file.transferTo(dest);

            String imageUrl = "http://localhost:" + serverPort + "/api/file/chat/" + fileName;

            return ApiResponse.success("上传成功", imageUrl);
        } catch (IOException e) {
            log.error("聊天图片上传失败", e);
            return ApiResponse.error(500, "上传失败：" + e.getMessage());
        }
    }
}
