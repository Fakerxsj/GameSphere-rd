package com.xsj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xsj.entity.Category;
import com.xsj.dto.response.CategoryGamesVO;
import com.xsj.dto.response.GameBriefVO;
import java.util.List;

public interface CategoryService extends IService<Category> {
    // 从 game 表提取类型并初始化分类数据
    int initCategoriesFromGames();

    List<CategoryGamesVO> getCategoryGames();
    List<GameBriefVO> getGamesByCategoryId(Long categoryId, Integer limit, Integer offset);
}
