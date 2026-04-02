package com.xsj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsj.entity.Game;
import com.xsj.entity.Rating;
import com.xsj.service.RatingService;
import com.xsj.mapper.RatingMapper;
import com.xsj.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl extends ServiceImpl<RatingMapper, Rating>
        implements RatingService {

    private final GameService gameService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGameRating(Long gameId) {
        LambdaQueryWrapper<Rating> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Rating::getGameId, gameId)
                .eq(Rating::getStatus, 1);

        java.util.List<Rating> ratings = list(wrapper);

        if (!ratings.isEmpty()) {
            BigDecimal avgScore = ratings.stream()
                    .map(Rating::getScore)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(ratings.size()), 2, BigDecimal.ROUND_HALF_UP);

            Game game = gameService.getById(gameId);
            if (game != null) {
                game.setRatingScore(avgScore);
                game.setRatingCount(ratings.size());
                gameService.updateById(game);
            }
        }
    }
}
