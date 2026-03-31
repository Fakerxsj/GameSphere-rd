package com.xsj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsj.entity.Rating;
import com.xsj.service.RatingService;
import com.xsj.mapper.RatingMapper;
import org.springframework.stereotype.Service;

/**
* @author 28227
* @description 针对表【rating(用户评分表)】的数据库操作Service实现
* @createDate 2026-03-31 10:06:07
*/
@Service
public class RatingServiceImpl extends ServiceImpl<RatingMapper, Rating>
    implements RatingService{

}




