package com.xsj.controller;

import com.xsj.crawler.IgdbCrawler;
import com.xsj.dto.response.ApiResponse;
import com.xsj.dto.response.GameListResponse;
import com.xsj.dto.response.RecommendationResponse;
import com.xsj.entity.Game;
import com.xsj.recommend.RecommendationEngine;
import com.xsj.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
@Tag(name = "推荐系统", description = "个性化推荐接口")
public class RecommendationController {

    private final RecommendationEngine recommendationEngine;
    private final GameService gameService;
    private final IgdbCrawler igdbCrawler;

    @GetMapping("/home")
    @Operation(summary = "获取首页推荐游戏")
    public ApiResponse<?> getHomeRecommendation(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        long totalGames = gameService.count();
        log.info("数据库中共有 {} 个游戏，userId={}", totalGames, userId);

        List<Game> games;
        String algorithm = "hot";
        String reason = "热门游戏推荐";
        boolean needCrawl = false;

        if (totalGames == 0) {
            log.info("数据库为空，触发爬取");
            needCrawl = true;
            games = Collections.emptyList();
        } else if (userId != null) {
            long userBehaviorCount = recommendationEngine.getUserBehaviorCount(userId);

            if (userBehaviorCount == 0) {
                log.info("新用户无行为数据，返回热门游戏");
                games = gameService.lambdaQuery()
                        .eq(Game::getStatus, 1)
                        .orderByDesc(Game::getRatingScore)
                        .last("LIMIT 40")
                        .list();
                algorithm = "hot";
                reason = "热门推荐（新用户）";
            } else {
                log.info("为登录用户 {} 生成个性化推荐", userId);
                games = recommendationEngine.recommendForUser(userId);
                algorithm = "hybrid";
                reason = "根据你的喜好推荐";

                if (games.size() < 10) {
                    log.info("推荐游戏不足（{}个），可能需要爬取更多", games.size());
                    if (totalGames < 50) {
                        needCrawl = true;
                    }
                }
            }
        } else {
            log.info("游客模式，返回热门游戏");
            games = gameService.lambdaQuery()
                    .eq(Game::getStatus, 1)
                    .orderByDesc(Game::getRatingScore)
                    .last("LIMIT 40")
                    .list();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("needCrawl", needCrawl);
        result.put("currentGames", games.size());

        if (!needCrawl) {
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

            result.put("response", response);
        }

        return ApiResponse.success(result);
    }

    @PostMapping("/crawl")
    @Operation(summary = "触发游戏爬取（同步）")
    public ApiResponse<?> triggerCrawl(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        try {
            log.info("开始爬取游戏数据，userId={}", userId);

            List<Game> existingGames = gameService.lambdaQuery().list();
            Set<Long> existingIgdbIds = existingGames.stream()
                    .map(Game::getIgdbId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            List<Game> newGames;
            if (userId != null && existingGames.size() > 10) {
                long userBehaviorCount = recommendationEngine.getUserBehaviorCount(userId);
                if (userBehaviorCount > 0) {
                    log.info("为有行为数据的用户爬取个性化游戏");
                    newGames = igdbCrawler.crawlPersonalizedGames(userId, 40, existingIgdbIds);
                } else {
                    log.info("新用户，爬取热门游戏");
                    newGames = igdbCrawler.crawlPopularGames(40);
                }
            } else {
                log.info("爬取热门游戏");
                newGames = igdbCrawler.crawlPopularGames(40);
            }

            int savedCount = 0;
            int skippedCount = 0;
            for (Game game : newGames) {
                if (game.getIgdbId() != null) {
                    Game existingGame = gameService.lambdaQuery()
                            .eq(Game::getIgdbId, game.getIgdbId())
                            .one();

                    if (existingGame == null) {
                        gameService.save(game);
                        savedCount++;
                        log.info("保存新游戏：{} (IGDB ID: {})", game.getName(), game.getIgdbId());
                    } else {
                        skippedCount++;
                        log.debug("游戏已存在，跳过：{} (IGDB ID: {})", game.getName(), game.getIgdbId());
                    }
                } else {
                    Game existingGame = gameService.lambdaQuery()
                            .eq(Game::getName, game.getName())
                            .eq(Game::getSourceSite, "IGDB")
                            .one();

                    if (existingGame == null) {
                        gameService.save(game);
                        savedCount++;
                    } else {
                        skippedCount++;
                    }
                }
            }

            log.info("爬取完成，总数：{}，新增：{}，跳过：{}", newGames.size(), savedCount, skippedCount);

            Map<String, Object> result = new HashMap<>();
            result.put("message", "爬取完成");
            result.put("total", newGames.size());
            result.put("saved", savedCount);
            result.put("skipped", skippedCount);

            return ApiResponse.success("爬取成功", result);
        } catch (Exception e) {
            log.error("爬取失败", e);
            return ApiResponse.error(500, "爬取失败：" + e.getMessage());
        }
    }

    @GetMapping("/crawl/status")
    @Operation(summary = "获取爬取状态")
    public ApiResponse<?> getCrawlStatus() {
        long totalGames = gameService.count();

        Map<String, Object> status = new HashMap<>();
        status.put("totalGames", totalGames);
        status.put("isReady", totalGames > 0);

        return ApiResponse.success(status);
    }
}
