package com.xsj.recommend.strategy;

import com.xsj.entity.Game;
import com.xsj.recommend.model.UserPreferenceModel;
import com.xsj.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContentBasedStrategy implements RecommendationStrategy {

    private final GameService gameService;

    @Override
    public List<Game> recommend(Long userId, Integer limit) {
        return Collections.emptyList();
    }

    public List<Game> recommend(Long userId, UserPreferenceModel preferenceModel, Integer limit) {
        log.info("执行基于内容的推荐算法，用户：{}", userId);

        if (preferenceModel == null) {
            return Collections.emptyList();
        }

        List<Game> allGames = gameService.lambdaQuery()
                .eq(Game::getStatus, 1)
                .list();

        Map<Long, Double> gameScoreMap = new HashMap<>();

        for (Game game : allGames) {
            double score = 0.0;

            if (game.getGameType() != null && preferenceModel.getCategoryPreferences() != null) {
                Long count = preferenceModel.getCategoryPreferences().get(game.getGameType());
                if (count != null) {
                    score += count * 2.0;
                }
            }

            if (game.getPlatform() != null && preferenceModel.getPlatformPreferences() != null) {
                Long count = preferenceModel.getPlatformPreferences().get(game.getPlatform());
                if (count != null) {
                    score += count * 1.5;
                }
            }

            if (game.getPrice() != null && preferenceModel.getPriceRangePreferences() != null) {
                double price = game.getPrice().doubleValue();
                Double range = Math.floor(price / 50.0) * 50.0;
                Long count = preferenceModel.getPriceRangePreferences().get(range);
                if (count != null) {
                    score += count * 1.0;
                }
            }

            if (score > 0) {
                gameScoreMap.put(game.getId(), score);
            }
        }

        List<Long> recommendedGameIds = gameScoreMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit.longValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (recommendedGameIds.isEmpty()) {
            return Collections.emptyList();
        }

        return gameService.listByIds(recommendedGameIds);
    }
}
