package com.xsj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsj.entity.GameCategory;
import com.xsj.service.GameCategoryService;
import com.xsj.mapper.GameCategoryMapper;
import org.springframework.stereotype.Service;

/**
* @author 28227
* @description 针对表【game_category(游戏分类关联表)】的数据库操作Service实现
* @createDate 2026-03-31 10:06:07
*/
@Service
public class GameCategoryServiceImpl extends ServiceImpl<GameCategoryMapper, GameCategory>
    implements GameCategoryService{

}




