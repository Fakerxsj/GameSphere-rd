package com.xsj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsj.entity.Comment;
import com.xsj.service.CommentService;
import com.xsj.mapper.CommentMapper;
import org.springframework.stereotype.Service;

/**
* @author 28227
* @description 针对表【comment(评论表)】的数据库操作Service实现
* @createDate 2026-03-31 10:06:07
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

}




