package com.xsj.mapper;

import com.xsj.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xsj.vo.CommentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {

    List<CommentVO> selectCommentsWithUser(@Param("gameId") Long gameId);

    List<CommentVO> selectReplies(@Param("parentId") Long parentId);
}
