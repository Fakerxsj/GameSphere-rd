package com.xsj.recommend.strategy;

import com.xsj.entity.Game;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HybridRecommendationStrategy implements RecommendationStrategy {

    @Value("${recommendation.algorithms.collaborative-filtering.weight:0.4}")
    private Double collaborativeWeight;

    @Value("${recommendation.algorithms.content-based.weight:0.4}")
    private Double contentBasedWeight;

    @Value("${recommendation.algorithms.hot-games.weight:0.2}")
    private Double hotGamesWeight;

    @Override
    public List<Game> recommend(Long userId, Integer limit) {
        return Collections.emptyList();
    }

    public List<Game> recommend(Long userId, List<Game> collaborativeGames,
                                List<Game> contentBasedGames, List<Game> hotGames) {
        log.info("执行混合推荐算法，权重：协同过滤={}, 基于内容={}, 热门={}",
                collaborativeWeight, contentBasedWeight, hotGamesWeight);

        Map<Long, Double> gameScoreMap = new HashMap<>();

        for (int i = 0; i < collaborativeGames.size(); i++) {
            Game game = collaborativeGames.get(i);
            double score = collaborativeWeight * (1.0 - (double) i / collaborativeGames.size());
            gameScoreMap.merge(game.getId(), score, Double::sum);
        }

        for (int i = 0; i < contentBasedGames.size(); i++) {
            Game game = contentBasedGames.get(i);
            double score = contentBasedWeight * (1.0 - (double) i / contentBasedGames.size());
            gameScoreMap.merge(game.getId(), score, Double::sum);
        }

        for (int i = 0; i < hotGames.size(); i++) {
            Game game = hotGames.get(i);
            double score = hotGamesWeight * (1.0 - (double) i / hotGames.size());
            gameScoreMap.merge(game.getId(), score, Double::sum);
        }

        List<Long> sortedGameIds = gameScoreMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        Map<Long, Game> gameMap = new HashMap<>();
        collaborativeGames.forEach(g -> gameMap.put(g.getId(), g));
        contentBasedGames.forEach(g -> gameMap.put(g.getId(), g));
        hotGames.forEach(g -> gameMap.put(g.getId(), g));

        return sortedGameIds.stream()
                .map(gameMap::get)
                .filter(Objects::nonNull)
                .limit(300)
                .collect(Collectors.toList());
    }
}
