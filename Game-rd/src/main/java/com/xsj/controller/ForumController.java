
package com.xsj.controller;

import com.xsj.dto.response.ApiResponse;
import com.xsj.dto.response.FourmSectionVO;
import com.xsj.dto.response.GameBriefVO;
import com.xsj.dto.response.GameForumVO;
import com.xsj.service.ForumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/forum")
@RequiredArgsConstructor
@Tag(name = "论坛管理", description = "论坛相关接口")
public class ForumController {

    private final ForumService forumService;

    @GetMapping("/home")
    @Operation(summary = "获取论坛首页数据")
    public ApiResponse<?> getForumHome(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        List<GameBriefVO> followedForums;
        List<GameBriefVO> hotForums;

        if (userId != null) {
            followedForums = forumService.getFollowedForums(userId);
        } else {
            followedForums = Collections.emptyList();
        }

        hotForums = forumService.getHotForums(10);

        Map<String, Object> result = new HashMap<>();
        result.put("followedForums", followedForums);
        result.put("hotForums", hotForums);

        return ApiResponse.success(result);
    }

    @GetMapping("/followed")
    @Operation(summary = "获取关注的游戏论坛")
    public ApiResponse<?> getFollowedForums(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }

        List<GameBriefVO> forums = forumService.getFollowedForums(userId);
        return ApiResponse.success(forums);
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门游戏论坛")
    public ApiResponse<?> getHotForums(
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        List<GameBriefVO> forums = forumService.getHotForums(limit);
        return ApiResponse.success(forums);
    }

    @GetMapping("/game/{gameId}")
    @Operation(summary = "获取游戏论坛详情")
    public ApiResponse<?> getGameForum(
            @PathVariable Long gameId,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");

        GameForumVO forumInfo = forumService.getGameForumInfo(gameId, userId);

        if (forumInfo == null) {
            return ApiResponse.error(404, "游戏不存在");
        }

        return ApiResponse.success(forumInfo);
    }

    @GetMapping("/game/{gameId}/sections")
    @Operation(summary = "获取游戏论坛版块列表")
    public ApiResponse<?> getGameSections(@PathVariable Long gameId) {
        List<FourmSectionVO> sections = forumService.getForumSections(gameId);
        return ApiResponse.success(sections);
    }
}
