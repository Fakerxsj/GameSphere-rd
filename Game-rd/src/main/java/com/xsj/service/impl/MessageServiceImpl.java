package com.xsj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsj.dto.response.ConversationVO;
import com.xsj.dto.response.MessageVO;
import com.xsj.entity.Message;
import com.xsj.entity.User;
import com.xsj.mapper.MessageMapper;
import com.xsj.service.MessageService;
import com.xsj.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private final MessageMapper messageMapper;
    private final UserService userService;

    @Override
    public MessageVO sendMessage(Long fromUserId, Long toUserId, String content, Integer messageType) {
        Message message = new Message();
        message.setFromUserId(fromUserId);
        message.setToUserId(toUserId);
        message.setContent(content);
        message.setMessageType(messageType);
        message.setIsRead(0);
        message.setCreateTime(new java.util.Date());

        save(message);

        return convertToVO(message);
    }

    @Override
    public List<ConversationVO> getConversationList(Long userId) {
        List<Message> conversations = messageMapper.selectConversationList(userId);
        List<ConversationVO> result = new ArrayList<>();

        for (Message msg : conversations) {
            Long otherUserId = msg.getFromUserId().equals(userId) ? msg.getToUserId() : msg.getFromUserId();
            User otherUser = userService.getById(otherUserId);
            if (otherUser == null) continue;

            int unreadCount = Math.toIntExact(lambdaQuery()
                    .eq(Message::getFromUserId, otherUserId)
                    .eq(Message::getToUserId, userId)
                    .eq(Message::getIsRead, 0)
                    .count());

            ConversationVO vo = new ConversationVO();
            vo.setUserId(otherUser.getId());
            vo.setNickname(otherUser.getNickname());
            vo.setAvatar(otherUser.getAvatar());
            vo.setLastMessage(msg.getContent());
            vo.setLastMessageTime(msg.getCreateTime());
            vo.setUnreadCount(unreadCount);

            result.add(vo);
        }

        return result;
    }

    @Override
    public List<MessageVO> getMessageHistory(Long userId, Long friendId, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Message> messages = messageMapper.selectMessageHistory(userId, friendId, pageSize, offset);
        List<MessageVO> result = new ArrayList<>();

        for (Message msg : messages) {
            result.add(convertToVO(msg));
        }

        return result;
    }

    @Override
    public int markAsRead(Long userId, Long friendId) {
        return messageMapper.markMessagesAsRead(userId, friendId);
    }

    @Override
    public int getUnreadCount(Long userId) {
        return messageMapper.countUnreadMessages(userId);
    }

    private MessageVO convertToVO(Message message) {
        MessageVO vo = new MessageVO();
        vo.setId(message.getId());
        vo.setFromUserId(message.getFromUserId());
        vo.setToUserId(message.getToUserId());
        vo.setContent(message.getContent());
        vo.setMessageType(message.getMessageType());
        vo.setIsRead(message.getIsRead());
        vo.setCreateTime(message.getCreateTime());

        User fromUser = userService.getById(message.getFromUserId());
        if (fromUser != null) {
            vo.setFromUserNickname(fromUser.getNickname());
            vo.setFromUserAvatar(fromUser.getAvatar());
        }

        return vo;
    }
}
