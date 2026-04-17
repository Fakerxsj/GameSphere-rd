package com.xsj.controller;

import com.xsj.dto.response.ApiResponse;
import com.xsj.dto.response.GameRankingResponse;
import com.xsj.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ranking")
@RequiredArgsConstructor
@Tag(name = "排行榜", description = "游戏排行榜接口")
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/hot")
    @Operation(summary = "获取热门排行榜")
    public ApiResponse<?> getHotRanking(
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer limit,
            @Parameter(description = "偏移量") @RequestParam(defaultValue = "0") Integer offset) {
        List<GameRankingResponse> list = rankingService.getHotRanking(limit, offset);

        Map<String, Object> result = new HashMap<>();
        result.put("games", list);
        result.put("total", list.size());
        result.put("rankingType", "hot");

        return ApiResponse.success(result);
    }

    @GetMapping("/rating")
    @Operation(summary = "获取评分排行榜")
    public ApiResponse<?> getRatingRanking(
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {
        List<GameRankingResponse> list = rankingService.getRatingRanking(limit, offset);

        Map<String, Object> result = new HashMap<>();
        result.put("games", list);
        result.put("total", list.size());
        result.put("rankingType", "rating");

        return ApiResponse.success(result);
    }

    @GetMapping("/download")
    @Operation(summary = "获取下载排行榜")
    public ApiResponse<?> getDownloadRanking(
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {
        List<GameRankingResponse> list = rankingService.getDownloadRanking(limit, offset);

        Map<String, Object> result = new HashMap<>();
        result.put("games", list);
        result.put("total", list.size());
        result.put("rankingType", "download");

        return ApiResponse.success(result);
    }

    @GetMapping("/follow")
    @Operation(summary = "获取关注排行榜")
    public ApiResponse<?> getFollowRanking(
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {
        List<GameRankingResponse> list = rankingService.getFollowRanking(limit, offset);

        Map<String, Object> result = new HashMap<>();
        result.put("games", list);
        result.put("total", list.size());
        result.put("rankingType", "follow");

        return ApiResponse.success(result);
    }
}
