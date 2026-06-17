package com.xsj.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xsj.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xsj.vo.AdminPostVO;
import com.xsj.vo.CommentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {

    List<CommentVO> selectCommentsWithUser(@Param("gameId") Long gameId);

    List<CommentVO> selectReplies(@Param("parentId") Long parentId);

    IPage<AdminPostVO> selectAdminPosts(Page<?> page, @Param("gameId") Long gameId, @Param("keyword") String keyword);
}
