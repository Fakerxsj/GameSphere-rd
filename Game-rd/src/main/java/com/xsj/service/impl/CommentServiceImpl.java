package com.xsj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsj.entity.Comment;
import com.xsj.service.CommentService;
import com.xsj.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
        implements CommentService {

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
}
