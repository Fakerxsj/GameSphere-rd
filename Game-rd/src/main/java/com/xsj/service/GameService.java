package com.xsj.service;

import com.xsj.entity.Game;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
* @author 28227
* @description 针对表【game(游戏表)】的数据库操作Service
* @createDate 2026-03-31 10:06:07
*/
public interface GameService extends IService<Game> {

    @Transactional(rollbackFor = Exception.class)
    void recordClick(Long gameId, Long userId);

    @Transactional(rollbackFor = Exception.class)
    void incrementFollowCount(Long gameId);
}
