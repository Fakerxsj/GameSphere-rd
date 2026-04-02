package com.xsj.service;

import com.xsj.entity.Rating;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
* @author 28227
* @description 针对表【rating(用户评分表)】的数据库操作Service
* @createDate 2026-03-31 10:06:07
*/
public interface RatingService extends IService<Rating> {

    @Transactional(rollbackFor = Exception.class)
    void updateGameRating(Long gameId);
}
