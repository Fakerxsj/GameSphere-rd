package com.xsj.controller;

import com.xsj.crawler.CrawlerManager;
import com.xsj.dto.response.ApiResponse;
import com.xsj.exception.CrawlerException;
import com.xsj.entity.Game;
import com.xsj.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/crawler")
@RequiredArgsConstructor
@Tag(name = "数据爬取管理", description = "游戏数据爬取接口")
public class CrawlerController {

    private final CrawlerManager crawlerManager;
    private final GameService gameService;

    @PostMapping("/taptap")
    @Operation(summary = "从 TapTap 爬取游戏数据")
    public ApiResponse<?> crawlFromTapTap(@RequestParam String url) {
        try {
            List<Game> games = crawlerManager.crawlFromTapTap(url);

            int savedCount = 0;
            for (Game game : games) {
                Game existingGame = gameService.lambdaQuery()
                        .eq(Game::getSourceUrl, game.getSourceUrl())
                        .one();

                if (existingGame == null) {
                    gameService.save(game);
                    savedCount++;
                } else {
                    game.setId(existingGame.getId());
                    gameService.updateById(game);
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("total", games.size());
            result.put("saved", savedCount);
            result.put("games", games);

            return ApiResponse.success("爬取成功", result);
        } catch (CrawlerException e) {
            log.error("爬取失败", e);
            return ApiResponse.error(500, "爬取失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("系统异常", e);
            return ApiResponse.error(500, "系统异常");
        }
    }

    @PostMapping("/taptap/list")
    @Operation(summary = "爬取 TapTap 游戏列表")
    public ApiResponse<?> crawlListFromTapTap(@RequestParam String listUrl) {
        try {
            List<Game> games = crawlerManager.crawlGameList(listUrl);

            int savedCount = 0;
            for (Game game : games) {
                Game existingGame = gameService.lambdaQuery()
                        .eq(Game::getSourceUrl, game.getSourceUrl())
                        .one();

                if (existingGame == null) {
                    gameService.save(game);
                    savedCount++;
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("total", games.size());
            result.put("saved", savedCount);

            return ApiResponse.success("列表爬取成功", result);
        } catch (CrawlerException e) {
            log.error("爬取失败", e);
            return ApiResponse.error(500, "爬取失败：" + e.getMessage());
        }
    }

    @PostMapping("/taptap/detail")
    @Operation(summary = "爬取 TapTap 游戏详情")
    public ApiResponse<?> crawlDetailFromTapTap(@RequestParam String detailUrl) {
        try {
            Game game = crawlerManager.crawlGameDetail(detailUrl);

            if (game == null) {
                return ApiResponse.error(400, "解析失败，未获取到游戏数据");
            }

            Game existingGame = gameService.lambdaQuery()
                    .eq(Game::getSourceUrl, detailUrl)
                    .one();

            if (existingGame != null) {
                game.setId(existingGame.getId());
                gameService.updateById(game);
            } else {
                gameService.save(game);
            }

            return ApiResponse.success("详情爬取成功", game);
        } catch (CrawlerException e) {
            log.error("爬取失败", e);
            return ApiResponse.error(500, "爬取失败：" + e.getMessage());
        }
    }

    @PostMapping("/taptap/popular")
    @Operation(summary = "爬取 TapTap 热门游戏")
    public ApiResponse<?> crawlPopularGames() {
        try {
            List<Game> games = crawlerManager.crawlFromTapTap("https://www.taptap.cn/discover/popular");

            int savedCount = 0;
            for (Game game : games) {
                Game existingGame = gameService.lambdaQuery()
                        .eq(Game::getSourceUrl, game.getSourceUrl())
                        .one();

                if (existingGame == null) {
                    gameService.save(game);
                    savedCount++;
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("total", games.size());
            result.put("saved", savedCount);

            return ApiResponse.success("热门游戏爬取成功", result);
        } catch (Exception e) {
            log.error("爬取失败", e);
            return ApiResponse.error(500, "爬取失败：" + e.getMessage());
        }
    }

    @GetMapping("/status")
    @Operation(summary = "获取爬虫状态")
    public ApiResponse<?> getCrawlerStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("enabled", true);
        status.put("source", "TapTap");
        status.put("baseUrl", "https://www.taptap.cn");

        long totalGames = gameService.count();
        status.put("totalGamesInDb", totalGames);

        return ApiResponse.success(status);
    }
}
