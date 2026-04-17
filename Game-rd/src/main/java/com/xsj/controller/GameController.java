package com.xsj.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xsj.dto.request.GameSearchRequest;
import com.xsj.dto.response.ApiResponse;
import com.xsj.dto.response.GameDetailResponse;
import com.xsj.dto.response.GameListResponse;
import com.xsj.dto.response.PageResponse;
import com.xsj.entity.Game;
import com.xsj.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
@Tag(name = "游戏管理", description = "游戏信息相关接口")
public class GameController {

    private final GameService gameService;

    @GetMapping("/list")
    @Operation(summary = "获取游戏列表")
    public ApiResponse<?> getGameList(GameSearchRequest request) {
        LambdaQueryWrapper<Game> wrapper = new LambdaQueryWrapper<>();

        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            wrapper.like(Game::getName, request.getKeyword());
        }

        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            wrapper.eq(Game::getGameType, request.getCategory());
        }

        if (request.getPlatform() != null && !request.getPlatform().isEmpty()) {
            wrapper.eq(Game::getPlatform, request.getPlatform());
        }

        if (request.getMinRating() != null) {
            wrapper.ge(Game::getRatingScore, request.getMinRating());
        }

        wrapper.eq(Game::getStatus, 1);

        boolean isAsc = "asc".equalsIgnoreCase(request.getSortOrder());
        switch (request.getSortBy()) {
            case "ratingScore":
                wrapper.orderBy(true, isAsc, Game::getRatingScore);
                break;
            case "followCount":
                wrapper.orderBy(true, isAsc, Game::getFollowCount);
                break;
            case "downloadCount":
                wrapper.orderBy(true, isAsc, Game::getDownloadCount);
                break;
            default:
                wrapper.orderBy(true, false, Game::getCreateTime);
        }

        Page<Game> page = gameService.page(
                new Page<>(request.getPageNum().longValue(), request.getPageSize().longValue()),
                wrapper
        );

        List<GameListResponse> records = page.getRecords().stream()
                .map(game -> {
                    GameListResponse response = new GameListResponse();
                    BeanUtils.copyProperties(game, response);
                    return response;
                })
                .collect(Collectors.toList());

        PageResponse<GameListResponse> pageResponse = PageResponse.of(
                records,
                page.getTotal(),
                page.getCurrent(),
                page.getSize()
        );

        return ApiResponse.success(pageResponse);
    }



    @GetMapping("/recommend")
    @Operation(summary = "获取推荐游戏列表")
    public ApiResponse<?> getRecommendGames(
            @RequestParam(defaultValue = "300") Integer limit,
            @RequestParam(required = false) String category
    ) {
        LambdaQueryWrapper<Game> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Game::getStatus, 1);

        if (category != null && !category.isEmpty()) {
            wrapper.eq(Game::getGameType, category);
        }

        wrapper.orderByDesc(Game::getRatingScore)
                .orderByDesc(Game::getFollowCount)
                .last("LIMIT " + limit);

        List<Game> games = gameService.list(wrapper);

        List<GameListResponse> responses = games.stream()
                .map(game -> {
                    GameListResponse response = new GameListResponse();
                    BeanUtils.copyProperties(game, response);
                    return response;
                })
                .collect(Collectors.toList());

        return ApiResponse.success(responses);
    }

    @PostMapping("/click/{id}")
    @Operation(summary = "记录游戏点击")
    public ApiResponse<?> clickGame(@PathVariable Long id, jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        if (userId != null) {
            gameService.recordClick(id, userId);
        }

        return ApiResponse.success(null);
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "获取游戏详情")
    public ApiResponse<?> getGameDetail(@PathVariable Long id, jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        Game game = gameService.getById(id);

        if (game == null) {
            return ApiResponse.error(404, "游戏不存在");
        }

        if (userId != null) {
            gameService.recordView(id, userId);
        }

        GameDetailResponse response = new GameDetailResponse();
        BeanUtils.copyProperties(game, response);

        if (game.getScreenshots() != null) {
            response.setScreenshots(Arrays.asList(game.getScreenshots().split(",")));
        }

        return ApiResponse.success(response);
    }

}
