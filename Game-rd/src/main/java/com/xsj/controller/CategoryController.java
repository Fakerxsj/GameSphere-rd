package com.xsj.controller;

import com.xsj.dto.response.ApiResponse;
import com.xsj.entity.Category;
import com.xsj.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@Tag(name = "分类管理", description = "游戏分类相关接口")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/list")
    @Operation(summary = "获取分类列表")
    public ApiResponse<?> getCategoryList() {
        List<Category> categories = categoryService.lambdaQuery()
                .eq(Category::getParentId, 0)
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSortOrder)
                .list();

        return ApiResponse.success(categories);
    }

    @GetMapping("/children/{parentId}")
    @Operation(summary = "获取子分类")
    public ApiResponse<?> getChildCategories(@PathVariable Long parentId) {
        List<Category> categories = categoryService.lambdaQuery()
                .eq(Category::getParentId, parentId)
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSortOrder)
                .list();

        return ApiResponse.success(categories);
    }
}
