package com.xsj.controller;

import com.xsj.dto.request.CommentRequest;
import com.xsj.dto.response.ApiResponse;
import com.xsj.entity.Comment;
import com.xsj.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@Tag(name = "评论管理", description = "游戏评论相关接口")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/submit")
    @Operation(summary = "提交评论")
    public ApiResponse<?> submitComment(
            @Valid @RequestBody CommentRequest request,
            HttpServletRequest httpRequest
    ) {
        Long userId = (Long) httpRequest.getAttribute("userId");

        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setGameId(request.getGameId());
        comment.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        comment.setContent(request.getContent());
        comment.setImages(request.getImages());
        comment.setLikeCount(0);
        comment.setReplyCount(0);
        comment.setStatus(1);
        comment.setIsTop(0);
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());

        commentService.save(comment);

        if (request.getParentId() != null && request.getParentId() > 0) {
            commentService.incrementReplyCount(request.getParentId());
        }

        return ApiResponse.success("评论成功", comment);
    }

    @GetMapping("/game/{gameId}")
    @Operation(summary = "获取游戏评论列表")
    public ApiResponse<?> getGameComments(@PathVariable Long gameId) {
        List<Comment> comments = commentService.lambdaQuery()
                .eq(Comment::getGameId, gameId)
                .eq(Comment::getParentId, 0)
                .eq(Comment::getStatus, 1)
                .orderByDesc(Comment::getCreateTime)
                .list();

        return ApiResponse.success(comments);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除评论")
    public ApiResponse<?> deleteComment(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        Comment comment = commentService.getById(id);
        if (comment == null) {
            return ApiResponse.error(404, "评论不存在");
        }

        if (!comment.getUserId().equals(userId)) {
            return ApiResponse.error(403, "无权限删除");
        }

        comment.setStatus(0);
        comment.setUpdateTime(new Date());
        commentService.updateById(comment);

        return ApiResponse.success("删除成功", null);
    }
}
