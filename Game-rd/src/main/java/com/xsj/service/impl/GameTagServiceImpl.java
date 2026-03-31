package com.xsj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsj.entity.GameTag;
import com.xsj.service.GameTagService;
import com.xsj.mapper.GameTagMapper;
import org.springframework.stereotype.Service;

/**
* @author 28227
* @description 针对表【game_tag(游戏标签表)】的数据库操作Service实现
* @createDate 2026-03-31 10:06:07
*/
@Service
public class GameTagServiceImpl extends ServiceImpl<GameTagMapper, GameTag>
    implements GameTagService{

}




