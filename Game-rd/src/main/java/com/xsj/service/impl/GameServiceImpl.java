package com.xsj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsj.entity.Game;
import com.xsj.service.GameService;
import com.xsj.mapper.GameMapper;
import org.springframework.stereotype.Service;

/**
* @author 28227
* @description 针对表【game(游戏表)】的数据库操作Service实现
* @createDate 2026-03-31 10:06:07
*/
@Service
public class GameServiceImpl extends ServiceImpl<GameMapper, Game>
    implements GameService{

}




