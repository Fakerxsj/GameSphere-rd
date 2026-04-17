package com.xsj.service.impl;

import com.xsj.dto.response.GameRankingResponse;
import com.xsj.entity.Game;
import com.xsj.service.GameService;
import com.xsj.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService {

    private final GameService gameService;

    @Override
    public List<GameRankingResponse> getHotRanking(Integer limit, Integer offset) {
        List<Game> games = gameService.lambdaQuery()
                .eq(Game::getStatus, 1)
                .orderByDesc(Game::getRatingScore)
                .orderByDesc(Game::getDownloadCount)
                .orderByDesc(Game::getFollowCount)
                .last("LIMIT " + limit + " OFFSET " + offset)
                .list();

        return convertToResponse(games);
    }

    @Override
    public List<GameRankingResponse> getRatingRanking(Integer limit, Integer offset) {
        List<Game> games = gameService.lambdaQuery()
                .eq(Game::getStatus, 1)
                .isNotNull(Game::getRatingScore)
                .orderByDesc(Game::getRatingScore)
                .orderByDesc(Game::getRatingCount)
                .last("LIMIT " + limit + " OFFSET " + offset)
                .list();

        return convertToResponse(games);
    }

    @Override
    public List<GameRankingResponse> getDownloadRanking(Integer limit, Integer offset) {
        List<Game> games = gameService.lambdaQuery()
                .eq(Game::getStatus, 1)
                .orderByDesc(Game::getDownloadCount)
                .orderByDesc(Game::getRatingScore)
                .last("LIMIT " + limit + " OFFSET " + offset)
                .list();

        return convertToResponse(games);
    }

    @Override
    public List<GameRankingResponse> getFollowRanking(Integer limit, Integer offset) {
        List<Game> games = gameService.lambdaQuery()
                .eq(Game::getStatus, 1)
                .orderByDesc(Game::getFollowCount)
                .orderByDesc(Game::getRatingScore)
                .last("LIMIT " + limit + " OFFSET " + offset)
                .list();

        return convertToResponse(games);
    }

    private List<GameRankingResponse> convertToResponse(List<Game> games) {
        return games.stream().map(game -> {
            GameRankingResponse response = new GameRankingResponse();
            BeanUtils.copyProperties(game, response);
            return response;
        }).collect(Collectors.toList());
    }
}
