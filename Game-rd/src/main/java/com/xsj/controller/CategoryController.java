package com.xsj.controller;

import com.xsj.dto.response.ApiResponse;
import com.xsj.dto.response.GameBriefVO;
import com.xsj.dto.response.CategoryGamesVO;
import com.xsj.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@Tag(name = "分类管理", description = "游戏分类相关接口")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/init")
    @Operation(summary = "从游戏数据初始化分类（仅运行一次）")
    public ApiResponse<?> initCategoriesFromGames() {
        try {
            int count = categoryService.initCategoriesFromGames();
            return ApiResponse.success("成功初始化 " + count + " 个分类");
        } catch (Exception e) {
            return ApiResponse.error(500, "初始化失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    @Operation(summary = "获取分类列表")
    public ApiResponse<?> getCategoryList() {
        return ApiResponse.success(categoryService.list());
    }

    @GetMapping("/home")
    @Operation(summary = "获取分类首页数据")
    public ApiResponse<?> getCategoryHome() {
        List<CategoryGamesVO> list = categoryService.getCategoryGames();
        return ApiResponse.success(list);
    }

    @GetMapping("/{id:\\d+}")
    @Operation(summary = "获取指定分类下的游戏")
    public ApiResponse<?> getCategoryGames(
            @Parameter(description = "分类 ID") @PathVariable Long id,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer limit,
            @Parameter(description = "偏移量") @RequestParam(defaultValue = "0") Integer offset) {

        List<GameBriefVO> list = categoryService.getGamesByCategoryId(id, limit, offset);

        Map<String, Object> result = new HashMap<>();
        result.put("games", list);
        result.put("total", list.size());
        return ApiResponse.success(result);
    }
}
