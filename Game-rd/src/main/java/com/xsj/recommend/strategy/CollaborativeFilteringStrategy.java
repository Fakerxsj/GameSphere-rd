package com.xsj.recommend.strategy;

import com.xsj.entity.Game;
import com.xsj.entity.UserBehavior;
import com.xsj.service.GameService;
import com.xsj.service.UserBehaviorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CollaborativeFilteringStrategy implements RecommendationStrategy {

    private final UserBehaviorService userBehaviorService;
    private final GameService gameService;

    @Override
    public List<Game> recommend(Long userId, Integer limit) {
        log.info("执行协同过滤推荐算法，用户：{}", userId);

        List<Long> similarUserIds = findSimilarUsers(userId);

        if (similarUserIds.isEmpty()) {
            log.info("未找到相似用户，返回空列表");
            return Collections.emptyList();
        }

        Set<Long> userViewedGameIds = getUserViewedGameIds(userId);

        List<UserBehavior> similarUserBehaviors = userBehaviorService.lambdaQuery()
                .in(UserBehavior::getUserId, similarUserIds)
                .eq(UserBehavior::getBehaviorType, "view")
                .list();

        Map<Long, Long> gameScoreMap = new HashMap<>();
        for (UserBehavior behavior : similarUserBehaviors) {
            Long gameId = behavior.getGameId();
            if (!userViewedGameIds.contains(gameId)) {
                gameScoreMap.merge(gameId, 1L, Long::sum);
            }
        }

        List<Long> recommendedGameIds = gameScoreMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(limit.longValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (recommendedGameIds.isEmpty()) {
            return Collections.emptyList();
        }

        return gameService.listByIds(recommendedGameIds);
    }

    private List<Long> findSimilarUsers(Long userId) {
        List<UserBehavior> currentUserBehaviors = userBehaviorService.lambdaQuery()
                .eq(UserBehavior::getUserId, userId)
                .eq(UserBehavior::getBehaviorType, "view")
                .last("LIMIT 100")
                .list();

        if (currentUserBehaviors.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> currentUserGameIds = currentUserBehaviors.stream()
                .map(UserBehavior::getGameId)
                .collect(Collectors.toSet());

        List<UserBehavior> allBehaviors = userBehaviorService.lambdaQuery()
                .eq(UserBehavior::getBehaviorType, "view")
                .ne(UserBehavior::getUserId, userId)
                .last("LIMIT 1000")
                .list();

        Map<Long, Set<Long>> userGameMap = new HashMap<>();
        for (UserBehavior behavior : allBehaviors) {
            userGameMap.computeIfAbsent(behavior.getUserId(), k -> new HashSet<>())
                    .add(behavior.getGameId());
        }

        return userGameMap.entrySet().stream()
                .map(entry -> {
                    Set<Long> intersection = new HashSet<>(currentUserGameIds);
                    intersection.retainAll(entry.getValue());

                    double similarity = (double) intersection.size() /
                            Math.max(currentUserGameIds.size(), entry.getValue().size());

                    return new AbstractMap.SimpleEntry<>(entry.getKey(), similarity);
                })
                .filter(entry -> entry.getValue() > 0.2)
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private Set<Long> getUserViewedGameIds(Long userId) {
        return userBehaviorService.lambdaQuery()
                .eq(UserBehavior::getUserId, userId)
                .eq(UserBehavior::getBehaviorType, "view")
                .list().stream()
                .map(UserBehavior::getGameId)
                .collect(Collectors.toSet());
    }
}
