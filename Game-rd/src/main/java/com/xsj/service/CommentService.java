package com.xsj.service;

import com.xsj.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xsj.vo.CommentVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentService extends IService<Comment> {

    @Transactional(rollbackFor = Exception.class)
    void incrementReplyCount(Long commentId);

    @Transactional(rollbackFor = Exception.class)
    void incrementLikeCount(Long commentId);

    @Transactional(rollbackFor = Exception.class)
    void incrementGameCommentCount(Long gameId);

    List<CommentVO> getCommentsWithUser(Long gameId);

    List<CommentVO> getRepliesWithUser(Long parentId);

    @Transactional(rollbackFor = Exception.class)
    void likeComment(Long commentId, Long userId);

    @Transactional(rollbackFor = Exception.class)
    void unlikeComment(Long commentId, Long userId);
}
