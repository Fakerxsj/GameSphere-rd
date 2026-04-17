package com.xsj.mapper;

import com.xsj.entity.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageMapper extends BaseMapper<Message> {

    List<Message> selectConversationList(@Param("userId") Long userId);

    List<Message> selectMessageHistory(@Param("userId") Long userId,
                                       @Param("friendId") Long friendId,
                                       @Param("limit") Integer limit,
                                       @Param("offset") Integer offset);

    int countUnreadMessages(@Param("userId") Long userId);

    int markMessagesAsRead(@Param("userId") Long userId, @Param("friendId") Long friendId);
}
