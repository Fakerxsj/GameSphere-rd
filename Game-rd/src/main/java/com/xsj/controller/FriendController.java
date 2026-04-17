package com.xsj.controller;

import com.xsj.dto.request.HandleFriendRequestDTO;
import com.xsj.dto.request.SendFriendRequestDTO;
import com.xsj.dto.request.UpdateRemarkDTO;
import com.xsj.dto.response.ApiResponse;
import com.xsj.dto.response.FriendRequestVO;
import com.xsj.dto.response.FriendVO;
import com.xsj.service.FriendRequestService;
import com.xsj.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friend")
@RequiredArgsConstructor
@Tag(name = "好友管理", description = "好友相关接口")
public class FriendController {

    private final FriendService friendService;
    private final FriendRequestService friendRequestService;

    @PostMapping("/request")
    @Operation(summary = "发送好友申请")
    public ApiResponse<?> sendFriendRequest(
            @Valid @RequestBody SendFriendRequestDTO dto,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }

        try {
            boolean result = friendRequestService.sendRequest(
                    userId,
                    dto.getToUserId(),
                    dto.getMessage(),
                    dto.getSourceCommentId()
            );
            return ApiResponse.success(result ? "好友申请已发送" : "发送失败");
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/request/handle")
    @Operation(summary = "处理好友申请")
    public ApiResponse<?> handleFriendRequest(
            @Valid @RequestBody HandleFriendRequestDTO dto,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }

        try {
            boolean result = friendRequestService.handleRequest(
                    dto.getRequestId(),
                    userId,
                    dto.getStatus()
            );
            return ApiResponse.success(result ? "处理成功" : "处理失败");
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/requests")
    @Operation(summary = "获取收到的好友申请")
    public ApiResponse<?> getFriendRequests(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }

        List<FriendRequestVO> requests = friendRequestService.getReceivedRequests(userId);
        return ApiResponse.success(requests);
    }

    @GetMapping("/list")
    @Operation(summary = "获取好友列表")
    public ApiResponse<?> getFriendList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }

        List<FriendVO> friends = friendService.getFriendList(userId);
        return ApiResponse.success(friends);
    }

    @DeleteMapping("/{friendId}")
    @Operation(summary = "删除好友")
    public ApiResponse<?> removeFriend(
            @PathVariable Long friendId,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }

        boolean result = friendService.removeFriend(userId, friendId);
        return ApiResponse.success(result ? "删除成功" : "删除失败");
    }

    @PutMapping("/{friendId}/remark")
    @Operation(summary = "修改好友备注")
    public ApiResponse<?> updateRemark(
            @PathVariable Long friendId,
            @Valid @RequestBody UpdateRemarkDTO dto,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }

        boolean result = friendService.updateRemark(userId, friendId, dto.getRemark());
        return ApiResponse.success(result ? "修改成功" : "修改失败");
    }
}
