package com.xsj.recommend.strategy;

import com.xsj.entity.Game;
import java.util.List;

public interface RecommendationStrategy {
    List<Game> recommend(Long userId, Integer limit);
}
