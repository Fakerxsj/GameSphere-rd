package com.xsj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xsj.dto.response.FourmSectionVO;
import com.xsj.dto.response.GameBriefVO;
import com.xsj.dto.response.GameForumVO;
import com.xsj.entity.Game;
import com.xsj.entity.UserFollow;
import com.xsj.service.ForumService;
import com.xsj.service.GameService;
import com.xsj.service.UserFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForumServiceImpl implements ForumService {

    private final GameService gameService;
    private final UserFollowService userFollowService;

    @Override
    public List<GameBriefVO> getFollowedForums(Long userId) {
        List<Long> gameIds = userFollowService.lambdaQuery()
                .eq(UserFollow::getUserId, userId)
                .eq(UserFollow::getFollowType, "game")
                .list()
                .stream()
                .map(UserFollow::getFollowTargetId)
                .collect(Collectors.toList());

        if (gameIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Game> games = gameService.lambdaQuery()
                .in(Game::getId, gameIds)
                .eq(Game::getStatus, 1)
                .list();

        return convertToGameBriefVO(games);
    }

    @Override
    public List<GameBriefVO> getHotForums(Integer limit) {
        List<Game> games = gameService.lambdaQuery()
                .eq(Game::getStatus, 1)
                .orderByDesc(Game::getCommentCount)
                .last("LIMIT " + limit)
                .list();

        if (games.isEmpty()) {
            games = gameService.lambdaQuery()
                    .eq(Game::getStatus, 1)
                    .last("ORDER BY RAND() LIMIT " + limit)
                    .list();
        }

        return convertToGameBriefVO(games);
    }

    @Override
    public GameForumVO getGameForumInfo(Long gameId, Long userId) {
        Game game = gameService.getById(gameId);
        if (game == null) {
            return null;
        }

        GameForumVO vo = new GameForumVO();
        vo.setGameId(game.getId());
        vo.setGameName(game.getName());
        vo.setCoverImage(game.getCoverImage());
        vo.setBackgroundImage(game.getBannerImage());
        vo.setFollowCount(game.getFollowCount() != null ? game.getFollowCount() : 0);
        vo.setPostCount(game.getCommentCount() != null ? game.getCommentCount() : 0);

        if (userId != null) {
            boolean isFollowed = userFollowService.lambdaQuery()
                    .eq(UserFollow::getUserId, userId)
                    .eq(UserFollow::getFollowType, "game")
                    .eq(UserFollow::getFollowTargetId, gameId)
                    .exists();
            vo.setIsFollowed(isFollowed);
        } else {
            vo.setIsFollowed(false);
        }

        vo.setSections(getForumSections(gameId));

        return vo;
    }

    @Override
    public List<FourmSectionVO> getForumSections(Long gameId) {
        List<FourmSectionVO> sections = new ArrayList<>();

        String[] sectionNames = {
                "综合讨论", "攻略专区", "BUG反馈", "同人创作", "组队招募"
        };

        for (int i = 0; i < sectionNames.length; i++) {
            FourmSectionVO section = new FourmSectionVO();
            section.setId((long) (i + 1));
            section.setGameId(gameId);
            section.setName(sectionNames[i]);
            section.setOrderNum(i);
            section.setPostCount(0);
            sections.add(section);
        }

        return sections;
    }

    private List<GameBriefVO> convertToGameBriefVO(List<Game> games) {
        return games.stream()
                .map(game -> {
                    GameBriefVO vo = new GameBriefVO();
                    vo.setId(game.getId());
                    vo.setName(game.getName());
                    vo.setCoverImage(game.getCoverImage());
                    vo.setBannerImage(game.getBannerImage());
                    vo.setCommentCount(game.getCommentCount() != null ? game.getCommentCount() : 0);
                    vo.setFollowCount(game.getFollowCount() != null ? game.getFollowCount() : 0);
                    vo.setRatingScore(game.getRatingScore());
                    vo.setDeveloper(game.getDeveloper());
                    return vo;
                })
                .collect(Collectors.toList());
    }
}
