package com.xsj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsj.entity.Comment;
import com.xsj.entity.Game;
import com.xsj.mapper.CommentMapper;
import com.xsj.service.CommentService;
import com.xsj.service.GameService;
import com.xsj.vo.CommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
        implements CommentService {

    private final CommentMapper commentMapper;
    private final GameService gameService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementReplyCount(Long commentId) {
        Comment comment = getById(commentId);
        if (comment != null) {
            comment.setReplyCount(comment.getReplyCount() + 1);
            updateById(comment);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementLikeCount(Long commentId) {
        Comment comment = getById(commentId);
        if (comment != null) {
            comment.setLikeCount(comment.getLikeCount() + 1);
            updateById(comment);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementGameCommentCount(Long gameId) {
        Game game = gameService.getById(gameId);
        if (game != null) {
            Integer currentCount = game.getCommentCount();
            game.setCommentCount(currentCount != null ? currentCount + 1 : 1);
            gameService.updateById(game);
        }
    }

    @Override
    public List<CommentVO> getCommentsWithUser(Long gameId) {
        List<CommentVO> comments = commentMapper.selectCommentsWithUser(gameId);

        comments.forEach(comment -> {
            if (comment.getImages() != null && !comment.getImages().isEmpty()) {
                comment.setImages(parseImages(String.valueOf(comment.getImages())));
            }
        });

        return comments;
    }

    @Override
    public List<CommentVO> getRepliesWithUser(Long parentId) {
        List<CommentVO> replies = commentMapper.selectReplies(parentId);

        replies.forEach(reply -> {
            if (reply.getImages() != null && !reply.getImages().isEmpty()) {
                reply.setImages(parseImages(String.valueOf(reply.getImages())));
            }
        });

        return replies;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likeComment(Long commentId, Long userId) {
        Comment comment = getById(commentId);
        if (comment != null) {
            comment.setLikeCount(comment.getLikeCount() + 1);
            updateById(comment);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlikeComment(Long commentId, Long userId) {
        Comment comment = getById(commentId);
        if (comment != null && comment.getLikeCount() > 0) {
            comment.setLikeCount(comment.getLikeCount() - 1);
            updateById(comment);
        }
    }

    private List<String> parseImages(String imagesStr) {
        if (imagesStr == null || imagesStr.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(imagesStr.split(","))
                .map(String::trim)
                .filter(url -> !url.isEmpty())
                .collect(Collectors.toList());
    }
}
