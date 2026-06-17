package com.xsj.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xsj.dto.response.ApiResponse;
import com.xsj.dto.response.PageResponse;
import com.xsj.entity.Comment;
import com.xsj.entity.Game;
import com.xsj.entity.User;
import com.xsj.mapper.CommentMapper;
import com.xsj.service.CommentService;
import com.xsj.service.GameService;
import com.xsj.service.UserService;
import com.xsj.vo.AdminPostVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "后台管理", description = "后台管理相关接口")
public class AdminController {

    private final GameService gameService;
    private final CommentService commentService;
    private final UserService userService;
    private final CommentMapper commentMapper;

    @GetMapping("/stats")
    @Operation(summary = "获取后台统计数据")
    public ApiResponse<?> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();

        // 游戏总数
        long totalGames = gameService.count(new LambdaQueryWrapper<Game>().eq(Game::getStatus, 1));
        stats.put("totalGames", totalGames);

        // 帖子总数（parent_id = 0 的根评论）
        long totalPosts = commentService.count(
                new LambdaQueryWrapper<Comment>().eq(Comment::getStatus, 1).eq(Comment::getParentId, 0));
        stats.put("totalPosts", totalPosts);

        // 评论总数（所有评论 + 回复）
        long totalComments = commentService.count(
                new LambdaQueryWrapper<Comment>().eq(Comment::getStatus, 1));
        stats.put("totalComments", totalComments);

        // 用户总数
        long totalUsers = userService.count();
        stats.put("totalUsers", totalUsers);

        // 热门游戏 TOP 5（按关注数排序）
        List<Game> hotGameList = gameService.lambdaQuery()
                .eq(Game::getStatus, 1)
                .orderByDesc(Game::getFollowCount)
                .orderByDesc(Game::getRatingScore)
                .last("LIMIT 5")
                .list();

        List<Map<String, Object>> hotGames = new ArrayList<>();
        for (int i = 0; i < hotGameList.size(); i++) {
            Game g = hotGameList.get(i);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("rank", i + 1);
            item.put("name", g.getName());
            item.put("views", g.getFollowCount() != null ? g.getFollowCount() : 0);
            item.put("rating", g.getRatingScore());
            hotGames.add(item);
        }
        stats.put("hotGames", hotGames);

        // 高赞帖子 TOP 5
        List<Comment> hotPostList = commentService.lambdaQuery()
                .eq(Comment::getStatus, 1)
                .eq(Comment::getParentId, 0)
                .orderByDesc(Comment::getLikeCount)
                .last("LIMIT 5")
                .list();

        List<Map<String, Object>> hotPosts = new ArrayList<>();
        for (int i = 0; i < hotPostList.size(); i++) {
            Comment c = hotPostList.get(i);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("rank", i + 1);
            // 截取内容前 50 个字符作为标题
            String title = c.getContent();
            if (title != null && title.length() > 50) {
                title = title.substring(0, 50) + "...";
            }
            item.put("title", title);
            item.put("likes", c.getLikeCount() != null ? c.getLikeCount() : 0);
            hotPosts.add(item);
        }
        stats.put("hotPosts", hotPosts);

        return ApiResponse.success(stats);
    }

    @GetMapping("/posts")
    @Operation(summary = "获取帖子列表（后台管理）")
    public ApiResponse<?> getAdminPosts(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long gameId,
            @RequestParam(required = false) String keyword
    ) {
        Page<AdminPostVO> page = new Page<>(pageNum, pageSize);
        IPage<AdminPostVO> result = commentMapper.selectAdminPosts(page, gameId, keyword);

        PageResponse<AdminPostVO> pageResponse = PageResponse.of(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
        );

        return ApiResponse.success(pageResponse);
    }

    @DeleteMapping("/posts/{id}")
    @Operation(summary = "管理员删除帖子（软删除）")
    public ApiResponse<?> deletePost(@PathVariable Long id) {
        Comment comment = commentService.getById(id);
        if (comment == null) {
            return ApiResponse.error(404, "帖子不存在");
        }
        comment.setStatus(0);
        comment.setUpdateTime(new Date());
        commentService.updateById(comment);
        return ApiResponse.success("删除成功", null);
    }

    @GetMapping("/forum-stats")
    @Operation(summary = "获取论坛热度排行")
    public ApiResponse<?> getForumStats() {
        List<Game> gameList = gameService.lambdaQuery()
                .eq(Game::getStatus, 1)
                .orderByDesc(Game::getCommentCount)
                .orderByDesc(Game::getFollowCount)
                .last("LIMIT 20")
                .list();

        List<Map<String, Object>> stats = new ArrayList<>();
        for (int i = 0; i < gameList.size(); i++) {
            Game g = gameList.get(i);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("rank", i + 1);
            item.put("gameName", g.getName());
            item.put("coverImage", g.getCoverImage());
            // 热度指数 = 关注数*3 + 评论数*2 + 评分人数
            int heatScore = (g.getFollowCount() != null ? g.getFollowCount() * 3 : 0)
                    + (g.getCommentCount() != null ? g.getCommentCount() * 2 : 0)
                    + (g.getRatingCount() != null ? g.getRatingCount() : 0);
            item.put("heatScore", heatScore);
            item.put("postCount", g.getCommentCount() != null ? g.getCommentCount() : 0);
            item.put("commentCount", g.getCommentCount() != null ? g.getCommentCount() : 0);
            item.put("favoriteCount", g.getFollowCount() != null ? g.getFollowCount() : 0);
            item.put("viewCount", g.getDownloadCount() != null ? g.getDownloadCount() : 0);
            stats.add(item);
        }

        return ApiResponse.success(stats);
    }

    @PostMapping("/init-game-stats")
    @Operation(summary = "初始化游戏统计数据（为0的字段填充合理随机值）")
    public ApiResponse<?> initGameStats() {
        List<Game> games = gameService.lambdaQuery()
                .eq(Game::getStatus, 1)
                .list();

        int updatedCount = 0;
        Random random = new Random();

        for (Game game : games) {
            boolean needUpdate = false;

            // 关注数 = 10万 ~ 100万（与游戏详情页前端随机范围一致）
            if (game.getFollowCount() == null || game.getFollowCount() == 0) {
                game.setFollowCount(random.nextInt(900000) + 100000);
                needUpdate = true;
            }

            // 下载量 = 50万 ~ 500万
            if (game.getDownloadCount() == null || game.getDownloadCount() == 0) {
                game.setDownloadCount(random.nextInt(4500000) + 500000);
                needUpdate = true;
            }

            // 评论数 = 1000 ~ 50000
            if (game.getCommentCount() == null || game.getCommentCount() == 0) {
                game.setCommentCount(random.nextInt(49000) + 1000);
                needUpdate = true;
            }

            // 评分人数 = 1万 ~ 10万（比原来大）
            if (game.getRatingCount() == null || game.getRatingCount() < 1000) {
                game.setRatingCount(random.nextInt(90000) + 10000);
                needUpdate = true;
            }

            if (needUpdate) {
                gameService.updateById(game);
                updatedCount++;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalGames", games.size());
        result.put("updatedCount", updatedCount);
        return ApiResponse.success(result);
    }
}
