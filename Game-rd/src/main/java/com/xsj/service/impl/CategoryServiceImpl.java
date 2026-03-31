package com.xsj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsj.entity.Category;
import com.xsj.service.CategoryService;
import com.xsj.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

/**
* @author 28227
* @description 针对表【category(游戏分类表)】的数据库操作Service实现
* @createDate 2026-03-31 10:06:07
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

}




