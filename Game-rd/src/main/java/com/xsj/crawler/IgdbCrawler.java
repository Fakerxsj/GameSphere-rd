package com.xsj.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import com.xsj.crawler.igdb.IgdbApiClient;
import com.xsj.crawler.igdb.IgdbDataConverter;
import com.xsj.entity.Game;
import com.xsj.entity.User;
import com.xsj.entity.UserBehavior;
import com.xsj.service.GameService;
import com.xsj.service.UserService;
import com.xsj.service.UserBehaviorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class IgdbCrawler {

    private final IgdbApiClient igdbApiClient;
    private final IgdbDataConverter dataConverter;
    private final UserBehaviorService userBehaviorService;
    private final GameService gameService;
    private final UserService userService;

    public List<Game> crawlPopularGames(int limit) {
        return crawlPopularGames(limit, 0);
    }

    public List<Game> crawlPopularGames(int limit, int offset) {
        log.info("从 IGDB 爬取热门游戏，数量：{}，偏移量：{}", limit, offset);

        try {
            JsonNode gamesNode = igdbApiClient.getPopularGames(limit, offset);

            log.info("IGDB API 返回节点数：{}", gamesNode != null ? gamesNode.size() : 0);

            List<Game> games = new ArrayList<>();
            int skippedNoCover = 0;
            int conversionFailed = 0;

            for (JsonNode gameNode : gamesNode) {
                Game game = dataConverter.convertToGame(gameNode);
                if (game == null) {
                    conversionFailed++;
                    continue;
                }
                if (game.getCoverImage() == null) {
                    skippedNoCover++;
                    continue;
                }
                games.add(game);
            }

            log.info("成功从 IGDB 爬取 {} 个游戏（转换失败：{}，无封面跳过：{}）",
                    games.size(), conversionFailed, skippedNoCover);
            return games;
        } catch (Exception e) {
            log.error("从 IGDB 爬取游戏失败", e);
            return new ArrayList<>();
        }
    }

    public List<Game> crawlPersonalizedGames(Long userId, int limit, Set<Long> existingIgdbIds) {
        log.info("为 userId={} 爬取个性化游戏，目标数量：{}，已存在IGDB IDs数量：{}", userId, limit, existingIgdbIds.size());

        try {
            User user = userService.getById(userId);
            int offset = user != null && user.getIgdbCrawlOffset() != null ? user.getIgdbCrawlOffset() : 0;

            log.info("用户 {} 当前偏移量：{}", userId, offset);

            List<UserBehavior> behaviors = userBehaviorService.lambdaQuery()
                    .eq(UserBehavior::getUserId, userId)
                    .orderByDesc(UserBehavior::getBehaviorTime)
                    .last("LIMIT 20")
                    .list();

            if (behaviors.isEmpty()) {
                log.info("用户无行为数据，爬取热门游戏");
                return crawlPopularGames(limit, offset);
            }

            List<Long> clickedGameDbIds = behaviors.stream()
                    .map(UserBehavior::getGameId)
                    .distinct()
                    .collect(Collectors.toList());

            List<Game> clickedGames = gameService.listByIds(clickedGameDbIds);

            Set<String> preferredGenres = new HashSet<>();
            Set<String> preferredPlatforms = new HashSet<>();

            for (Game game : clickedGames) {
                if (game.getGameType() != null) {
                    preferredGenres.addAll(Arrays.asList(game.getGameType().split(",\\s*")));
                }
                if (game.getPlatform() != null) {
                    preferredPlatforms.addAll(Arrays.asList(game.getPlatform().split(",\\s*")));
                }
            }

            log.info("用户偏好类型：{}，偏好平台：{}", preferredGenres, preferredPlatforms);

            if (preferredGenres.isEmpty()) {
                log.info("无法提取用户偏好，返回热门游戏");
                return crawlPopularGames(limit, offset);
            }

            List<Game> personalizedGames = new ArrayList<>();
            int totalFetched = 0;
            int maxAttempts = 3;

            for (int attempt = 0; attempt < maxAttempts && personalizedGames.size() < limit; attempt++) {
                if (attempt > 0) {
                    log.info("第 {} 次尝试，使用偏移量 {} 获取更多游戏", attempt + 1, offset);
                }

                log.info("开始从 IGDB 获取热门游戏列表用于筛选（offset: {}）...", offset);
                JsonNode gamesNode = igdbApiClient.getPopularGames(limit * 3, offset);

                log.info("IGDB 返回的游戏节点数：{}", gamesNode != null ? gamesNode.size() : 0);

                if (gamesNode == null || gamesNode.size() == 0) {
                    log.warn("IGDB 返回空数据，停止尝试");
                    break;
                }

                int processedInBatch = 0;
                int matchedInBatch = 0;

                for (JsonNode gameNode : gamesNode) {
                    totalFetched++;

                    boolean genreMatch = false;
                    if (gameNode.has("genres") && gameNode.get("genres").isArray()) {
                        for (JsonNode genre : gameNode.get("genres")) {
                            if (genre.has("name") && preferredGenres.contains(genre.get("name").asText())) {
                                genreMatch = true;
                                break;
                            }
                        }
                    }

                    if (genreMatch) {
                        Game game = dataConverter.convertToGame(gameNode);

                        if (game == null) continue;
                        if (game.getCoverImage() == null) continue;
                        if (game.getIgdbId() == null) continue;

                        if (existingIgdbIds.contains(game.getIgdbId())) {
                            log.debug("游戏 {} (IGDB ID: {}) 已存在，跳过", game.getName(), game.getIgdbId());
                            continue;
                        }

                        personalizedGames.add(game);
                        matchedInBatch++;
                    }

                    if (personalizedGames.size() >= limit) break;
                    processedInBatch++;
                }

                log.info("第 {} 批处理完成 - 处理：{}, 匹配：{}, 当前总数：{}",
                        attempt + 1, processedInBatch, matchedInBatch, personalizedGames.size());

                offset += gamesNode.size();
            }

            if (personalizedGames.size() < limit) {
                int remaining = limit - personalizedGames.size();
                log.info("个性化游戏不足（{}个），补充 {} 个热门游戏", personalizedGames.size(), remaining);

                int additionalAdded = 0;
                for (int attempt = 0; attempt < 2 && additionalAdded < remaining; attempt++) {
                    JsonNode gamesNode = igdbApiClient.getPopularGames(limit * 2, offset);
                    if (gamesNode == null || gamesNode.size() == 0) break;

                    for (JsonNode gameNode : gamesNode) {
                        if (personalizedGames.size() >= limit) break;

                        Game game = dataConverter.convertToGame(gameNode);
                        if (game != null && game.getCoverImage() != null &&
                                game.getIgdbId() != null && !existingIgdbIds.contains(game.getIgdbId())) {

                            boolean alreadyAdded = personalizedGames.stream()
                                    .anyMatch(g -> g.getIgdbId() != null && g.getIgdbId().equals(game.getIgdbId()));

                            if (!alreadyAdded) {
                                personalizedGames.add(game);
                                additionalAdded++;
                            }
                        }
                    }
                    offset += gamesNode.size();
                }
                log.info("第二轮补充完成，新增：{} 个", additionalAdded);
            }

            user.setIgdbCrawlOffset(offset);
            userService.updateById(user);
            log.info("✅ 更新用户 {} 偏移量为：{}", userId, offset);

            log.info("✅ 成功爬取 {} 个个性化游戏（目标：{}，总共尝试获取：{}，最终偏移量：{}）",
                    personalizedGames.size(), limit, totalFetched, offset);
            return personalizedGames;
        } catch (Exception e) {
            log.error("❌ 爬取个性化游戏失败", e);
            return crawlPopularGames(limit);
        }
    }

    public List<Game> searchGames(String query, int limit) {
        log.info("从 IGDB 搜索游戏：{}", query);

        try {
            JsonNode gamesNode = igdbApiClient.searchGames(query, limit);

            List<Game> games = new ArrayList<>();
            for (JsonNode gameNode : gamesNode) {
                Game game = dataConverter.convertToGame(gameNode);
                if (game != null && game.getCoverImage() != null) {
                    games.add(game);
                }
            }

            log.info("从 IGDB 搜索到 {} 个游戏", games.size());
            return games;
        } catch (Exception e) {
            log.error("从 IGDB 搜索游戏失败：{}", query, e);
            return new ArrayList<>();
        }
    }
}
