package com.xsj.controller;

import com.xsj.dto.response.ApiResponse;
import com.xsj.dto.response.GameListResponse;
import com.xsj.dto.response.RecommendationResponse;
import com.xsj.entity.Game;
import com.xsj.recommend.RecommendationEngine;
import com.xsj.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
@Tag(name = "推荐系统", description = "个性化推荐接口")
public class RecommendationController {

    private final RecommendationEngine recommendationEngine;
    private final GameService gameService;

    @GetMapping("/home")
    @Operation(summary = "获取首页推荐游戏")
    public ApiResponse<?> getHomeRecommendation(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        List<Game> games;
        String algorithm = "hot";
        String reason = "热门游戏推荐";

        if (userId != null) {
            log.info("为登录用户 {} 生成个性化推荐", userId);
            games = recommendationEngine.recommendForUser(userId);
            algorithm = "hybrid";
            reason = "根据你的喜好推荐";
        } else {
            log.info("游客模式，返回热门游戏");
            games = gameService.lambdaQuery()
                    .eq(Game::getStatus, 1)
                    .orderByDesc(Game::getFollowCount)
                    .orderByDesc(Game::getRatingScore)
                    .last("LIMIT 300")
                    .list();
        }

        List<GameListResponse> gameResponses = games.stream()
                .map(game -> {
                    GameListResponse response = new GameListResponse();
                    BeanUtils.copyProperties(game, response);
                    return response;
                })
                .collect(Collectors.toList());

        RecommendationResponse response = new RecommendationResponse();
        response.setGames(gameResponses);
        response.setAlgorithm(algorithm);
        response.setReason(reason);
        response.setTotal(gameResponses.size());

        return ApiResponse.success(response);
    }

    @GetMapping("/similar/{gameId}")
    @Operation(summary = "获取相似游戏推荐")
    public ApiResponse<?> getSimilarGames(@PathVariable Long gameId) {
        Game game = gameService.getById(gameId);
        if (game == null) {
            return ApiResponse.error(404, "游戏不存在");
        }

        List<Game> similarGames = gameService.lambdaQuery()
                .eq(Game::getStatus, 1)
                .eq(Game::getGameType, game.getGameType())
                .ne(Game::getId, gameId)
                .orderByDesc(Game::getRatingScore)
                .last("LIMIT 20")
                .list();

        List<GameListResponse> responses = similarGames.stream()
                .map(g -> {
                    GameListResponse response = new GameListResponse();
                    BeanUtils.copyProperties(g, response);
                    return response;
                })
                .collect(Collectors.toList());

        return ApiResponse.success(responses);
    }
}
