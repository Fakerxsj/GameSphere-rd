package com.xsj.recommend.strategy;

import com.xsj.entity.Game;
import com.xsj.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HotGameStrategy implements RecommendationStrategy {

    private final GameService gameService;

    @Override
    public List<Game> recommend(Long userId, Integer limit) {
        log.info("执行热门游戏推荐算法");

        return gameService.lambdaQuery()
                .eq(Game::getStatus, 1)
                .orderByDesc(Game::getFollowCount)
                .orderByDesc(Game::getRatingScore)
                .orderByDesc(Game::getDownloadCount)
                .last("LIMIT " + limit)
                .list();
    }
}
