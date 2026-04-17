package com.xsj.service;

import com.xsj.dto.response.ConversationVO;
import com.xsj.dto.response.MessageVO;
import com.xsj.entity.Message;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface MessageService extends IService<Message> {

    MessageVO sendMessage(Long fromUserId, Long toUserId, String content, Integer messageType);

    List<ConversationVO> getConversationList(Long userId);

    List<MessageVO> getMessageHistory(Long userId, Long friendId, Integer pageNum, Integer pageSize);

    int markAsRead(Long userId, Long friendId);

    int getUnreadCount(Long userId);
}
