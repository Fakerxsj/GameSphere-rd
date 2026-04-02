package com.xsj.controller;

import com.xsj.dto.response.ApiResponse;
import com.xsj.entity.Rating;
import com.xsj.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/rating")
@RequiredArgsConstructor
@Tag(name = "评分管理", description = "游戏评分相关接口")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/submit")
    @Operation(summary = "提交游戏评分")
    public ApiResponse<?> submitRating(
            @RequestParam Long gameId,
            @RequestParam Double score,
            @RequestParam(required = false) String content,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }

        Rating existingRating = ratingService.lambdaQuery()
                .eq(Rating::getUserId, userId)
                .eq(Rating::getGameId, gameId)
                .one();

        if (existingRating != null) {
            existingRating.setScore(new java.math.BigDecimal(String.valueOf(score)));
            if (content != null) {
                existingRating.setContent(content);
            }
            existingRating.setUpdateTime(new Date());
            ratingService.updateById(existingRating);

            ratingService.updateGameRating(gameId);

            return ApiResponse.success("评分更新成功", null);
        } else {
            Rating rating = new Rating();
            rating.setUserId(userId);
            rating.setGameId(gameId);
            rating.setScore(new java.math.BigDecimal(String.valueOf(score)));
            rating.setContent(content);
            rating.setCreateTime(new Date());
            rating.setUpdateTime(new Date());
            rating.setStatus(1);

            ratingService.save(rating);

            ratingService.updateGameRating(gameId);

            return ApiResponse.success("评分成功", null);
        }
    }

    @GetMapping("/game/{gameId}")
    @Operation(summary = "获取游戏评分列表")
    public ApiResponse<?> getGameRatings(@PathVariable Long gameId) {
        List<Rating> ratings = ratingService.lambdaQuery()
                .eq(Rating::getGameId, gameId)
                .eq(Rating::getStatus, 1)
                .orderByDesc(Rating::getCreateTime)
                .list();

        return ApiResponse.success(ratings);
    }
}
