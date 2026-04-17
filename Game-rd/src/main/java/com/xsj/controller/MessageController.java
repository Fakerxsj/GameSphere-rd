package com.xsj.controller;

import com.xsj.dto.request.SendMessageDTO;
import com.xsj.dto.response.ApiResponse;
import com.xsj.dto.response.ConversationVO;
import com.xsj.dto.response.MessageVO;
import com.xsj.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
@Tag(name = "消息管理", description = "消息相关接口")
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    @Operation(summary = "发送消息（HTTP方式，WebSocket失败时使用）")
    public ApiResponse<?> sendMessage(
            @Valid @RequestBody SendMessageDTO dto,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }

        MessageVO message = messageService.sendMessage(
                userId,
                dto.getToUserId(),
                dto.getContent(),
                dto.getMessageType()
        );
        return ApiResponse.success("发送成功", message);
    }

    @GetMapping("/conversations")
    @Operation(summary = "获取会话列表")
    public ApiResponse<?> getConversations(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }

        List<ConversationVO> conversations = messageService.getConversationList(userId);
        return ApiResponse.success(conversations);
    }

    @GetMapping("/history/{friendId}")
    @Operation(summary = "获取聊天记录")
    public ApiResponse<?> getMessageHistory(
            @PathVariable Long friendId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }

        List<MessageVO> messages = messageService.getMessageHistory(userId, friendId, pageNum, pageSize);
        return ApiResponse.success(messages);
    }

    @PutMapping("/read/{friendId}")
    @Operation(summary = "标记消息为已读")
    public ApiResponse<?> markAsRead(
            @PathVariable Long friendId,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }

        int count = messageService.markAsRead(userId, friendId);
        return ApiResponse.success("已标记 " + count + " 条消息为已读");
    }

    @GetMapping("/unread/count")
    @Operation(summary = "获取未读消息总数")
    public ApiResponse<?> getUnreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }

        int count = messageService.getUnreadCount(userId);
        return ApiResponse.success(count);
    }
}
