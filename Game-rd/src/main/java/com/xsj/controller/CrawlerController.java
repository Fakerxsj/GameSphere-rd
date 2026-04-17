package com.xsj.controller;

import com.xsj.crawler.CrawlerManager;
import com.xsj.crawler.IgdbCrawler;
import com.xsj.dto.response.ApiResponse;
import com.xsj.exception.CrawlerException;
import com.xsj.entity.Game;
import com.xsj.entity.GameTag;
import com.xsj.entity.GameTagRelation;
import com.xsj.service.GameService;
import com.xsj.service.GameTagService;
import com.xsj.service.GameTagRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/crawler")
@RequiredArgsConstructor
@Tag(name = "数据爬取管理", description = "游戏数据爬取接口")
public class CrawlerController {

    private final CrawlerManager crawlerManager;
    private final GameService gameService;
    private final GameTagService gameTagService;
    private final GameTagRelationService gameTagRelationService;
    private final IgdbCrawler igdbCrawler;

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

                    saveGameTags(game.getId(), url);
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

                    if (game.getSourceUrl() != null) {
                        saveGameTags(game.getId(), game.getSourceUrl());
                    }
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

            saveGameTags(game.getId(), detailUrl);

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

                    if (game.getSourceUrl() != null) {
                        saveGameTags(game.getId(), game.getSourceUrl());
                    }
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


    @PostMapping("/igdb/popular")
    @Operation(summary = "从 IGDB 爬取热门游戏")
    public ApiResponse<?> crawlIgdbPopularGames(@RequestParam(defaultValue = "30") int limit) {
        try {
            log.info("开始从 IGDB 爬取 {} 个热门游戏", limit);

            List<Game> games = igdbCrawler.crawlPopularGames(limit);

            int savedCount = 0;
            for (Game game : games) {
                Game existingGame = gameService.lambdaQuery()
                        .eq(Game::getName, game.getName())
                        .eq(Game::getSourceSite, "IGDB")
                        .one();

                if (existingGame == null) {
                    gameService.save(game);
                    savedCount++;
                    log.info("保存游戏：{}", game.getName());
                } else {
                    log.info("游戏已存在，跳过：{}", game.getName());
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("total", games.size());
            result.put("saved", savedCount);

            return ApiResponse.success("IGDB 热门游戏爬取成功", result);
        } catch (Exception e) {
            log.error("IGDB 爬取失败", e);
            return ApiResponse.error(500, "爬取失败：" + e.getMessage());
        }
    }

    @PostMapping("/igdb/search")
    @Operation(summary = "从 IGDB 搜索游戏")
    public ApiResponse<?> searchIgdbGames(@RequestParam String query,
                                          @RequestParam(defaultValue = "20") int limit) {
        try {
            log.info("从 IGDB 搜索游戏：{}", query);

            List<Game> games = igdbCrawler.searchGames(query, limit);

            int savedCount = 0;
            for (Game game : games) {
                Game existingGame = gameService.lambdaQuery()
                        .eq(Game::getName, game.getName())
                        .eq(Game::getSourceSite, "IGDB")
                        .one();

                if (existingGame == null) {
                    gameService.save(game);
                    savedCount++;
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("total", games.size());
            result.put("saved", savedCount);
            result.put("games", games);

            return ApiResponse.success("搜索成功", result);
        } catch (Exception e) {
            log.error("IGDB 搜索失败", e);
            return ApiResponse.error(500, "搜索失败：" + e.getMessage());
        }
    }

    private void saveGameTags(Long gameId, String detailUrl) {
        try {
            List<String> tagNames = crawlerManager.extractTagsFromDetail(detailUrl);

            if (tagNames == null || tagNames.isEmpty()) {
                log.warn("未提取到标签，游戏ID：{}", gameId);
                return;
            }

            for (String tagName : tagNames) {
                if (tagName == null || tagName.trim().isEmpty()) {
                    continue;
                }

                tagName = tagName.trim();

                GameTag tag = gameTagService.lambdaQuery()
                        .eq(GameTag::getName, tagName)
                        .one();

                if (tag == null) {
                    tag = new GameTag();
                    tag.setName(tagName);
                    tag.setStatus(1);
                    tag.setCreateTime(new Date());
                    tag.setUpdateTime(new Date());
                    gameTagService.save(tag);
                }

                boolean exists = gameTagRelationService.lambdaQuery()
                        .eq(GameTagRelation::getGameId, gameId)
                        .eq(GameTagRelation::getTagId, tag.getId())
                        .exists();

                if (!exists) {
                    GameTagRelation relation = new GameTagRelation();
                    relation.setGameId(gameId);
                    relation.setTagId(tag.getId());
                    relation.setCreateTime(new Date());
                    gameTagRelationService.save(relation);

                    log.debug("保存标签关系：游戏ID={}, 标签={}", gameId, tagName);
                }
            }

            log.info("游戏ID={} 保存了 {} 个标签", gameId, tagNames.size());
        } catch (Exception e) {
            log.error("保存标签失败，游戏ID：{}", gameId, e);
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
