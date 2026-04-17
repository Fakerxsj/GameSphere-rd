package com.xsj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsj.entity.Game;
import com.xsj.entity.UserBehavior;
import com.xsj.service.GameService;
import com.xsj.mapper.GameMapper;
import com.xsj.service.UserBehaviorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameServiceImpl extends ServiceImpl<GameMapper, Game>
        implements GameService {

    private final UserBehaviorService userBehaviorService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordClick(Long gameId, Long userId) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setGameId(gameId);
        behavior.setBehaviorType("click");
        behavior.setBehaviorTime(new java.util.Date());
        userBehaviorService.save(behavior);

        Game game = getById(gameId);
        if (game != null) {
            game.setDownloadCount(game.getDownloadCount() + 1);
            updateById(game);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementFollowCount(Long gameId) {
        Game game = getById(gameId);
        if (game != null) {
            game.setFollowCount(game.getFollowCount() + 1);
            updateById(game);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordView(Long gameId, Long userId) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setGameId(gameId);
        behavior.setBehaviorType("view");
        behavior.setBehaviorTime(new java.util.Date());
        userBehaviorService.save(behavior);
    }
}
