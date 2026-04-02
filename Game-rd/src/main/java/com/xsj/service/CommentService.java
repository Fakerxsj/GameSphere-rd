package com.xsj.service;

import com.xsj.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
* @author 28227
* @description 针对表【comment(评论表)】的数据库操作Service
* @createDate 2026-03-31 10:06:07
*/
public interface CommentService extends IService<Comment> {

    @Transactional(rollbackFor = Exception.class)
    void incrementReplyCount(Long commentId);

    @Transactional(rollbackFor = Exception.class)
    void incrementLikeCount(Long commentId);
}
