package com.xsj.mapper;

import com.xsj.entity.Friend;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FriendMapper extends BaseMapper<Friend> {

    List<Friend> selectFriendsByUserId(@Param("userId") Long userId);

    boolean existsFriendship(@Param("userId") Long userId, @Param("friendId") Long friendId);

    int deleteFriendship(@Param("userId") Long userId, @Param("friendId") Long friendId);
}


