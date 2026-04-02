package com.xsj.recommend;

import com.xsj.entity.Game;
import com.xsj.entity.UserBehavior;
import com.xsj.recommend.model.GameSimilarityModel;
import com.xsj.recommend.model.UserPreferenceModel;
import com.xsj.recommend.strategy.CollaborativeFilteringStrategy;
import com.xsj.recommend.strategy.ContentBasedStrategy;
import com.xsj.recommend.strategy.HotGameStrategy;
import com.xsj.recommend.strategy.HybridRecommendationStrategy;
import com.xsj.service.GameService;
import com.xsj.service.UserBehaviorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationEngine {

    private final UserBehaviorService userBehaviorService;
    private final GameService gameService;
    private final CollaborativeFilteringStrategy collaborativeFilteringStrategy;
    private final ContentBasedStrategy contentBasedStrategy;
    private final HotGameStrategy hotGameStrategy;
    private final HybridRecommendationStrategy hybridRecommendationStrategy;

    @Value("${recommendation.min-games}")
    private Integer minGames;

    public List<Game> recommendForUser(Long userId) {
        log.info("为用户 {} 生成推荐列表", userId);

        List<UserBehavior> userBehaviors = userBehaviorService.lambdaQuery()
                .eq(UserBehavior::getUserId, userId)
                .orderByDesc(UserBehavior::getBehaviorTime)
                .list();

        if (userBehaviors.isEmpty()) {
            log.info("用户 {} 无行为数据，返回热门游戏", userId);
            return hotGameStrategy.recommend(userId, minGames);
        }

        UserPreferenceModel preferenceModel = buildUserPreferenceModel(userId, userBehaviors);

        List<Game> collaborativeGames = collaborativeFilteringStrategy.recommend(userId, minGames / 2);
        List<Game> contentBasedGames = contentBasedStrategy.recommend(userId, preferenceModel, minGames / 2);
        List<Game> hotGames = hotGameStrategy.recommend(userId, minGames / 3);

        List<Game> hybridGames = hybridRecommendationStrategy.recommend(
                userId,
                collaborativeGames,
                contentBasedGames,
                hotGames
        );

        Set<Long> viewedGameIds = userBehaviors.stream()
                .map(UserBehavior::getGameId)
                .collect(Collectors.toSet());

        return hybridGames.stream()
                .filter(game -> !viewedGameIds.contains(game.getId()))
                .limit(minGames)
                .collect(Collectors.toList());
    }

    private UserPreferenceModel buildUserPreferenceModel(Long userId, List<UserBehavior> behaviors) {
        UserPreferenceModel model = new UserPreferenceModel();
        model.setUserId(userId);

        Map<String, Long> categoryCount = new HashMap<>();
        Map<String, Long> tagCount = new HashMap<>();
        Map<String, Long> platformCount = new HashMap<>();
        Map<Double, Long> priceRangeCount = new HashMap<>();

        List<Long> gameIds = behaviors.stream()
                .map(UserBehavior::getGameId)
                .distinct()
                .collect(Collectors.toList());

        List<Game> games = gameService.listByIds(gameIds);

        for (Game game : games) {
            if (game.getGameType() != null) {
                categoryCount.merge(game.getGameType(), 1L, Long::sum);
            }

            if (game.getPlatform() != null) {
                platformCount.merge(game.getPlatform(), 1L, Long::sum);
            }

            if (game.getPrice() != null) {
                double price = game.getPrice().doubleValue();
                Double range = Math.floor(price / 50.0) * 50.0;
                priceRangeCount.merge(range, 1L, Long::sum);
            }
        }

        model.setCategoryPreferences(categoryCount);
        model.setTagPreferences(tagCount);
        model.setPlatformPreferences(platformCount);
        model.setPriceRangePreferences(priceRangeCount);

        long viewCount = behaviors.stream()
                .filter(b -> "view".equals(b.getBehaviorType()))
                .count();
        long collectCount = behaviors.stream()
                .filter(b -> "collect".equals(b.getBehaviorType()))
                .count();
        long downloadCount = behaviors.stream()
                .filter(b -> "download".equals(b.getBehaviorType()))
                .count();

        model.setViewScore(viewCount);
        model.setCollectScore(collectCount * 5);
        model.setDownloadScore(downloadCount * 10);

        return model;
    }
}
