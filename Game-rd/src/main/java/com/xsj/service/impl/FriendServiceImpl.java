package com.xsj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsj.dto.response.FriendVO;
import com.xsj.entity.Friend;
import com.xsj.entity.User;
import com.xsj.mapper.FriendMapper;
import com.xsj.service.FriendService;
import com.xsj.service.MessageService;
import com.xsj.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {

    private final FriendMapper friendMapper;
    private final UserService userService;
    private final MessageService messageService;

    @Override
    public List<FriendVO> getFriendList(Long userId) {
        List<Friend> friends = friendMapper.selectFriendsByUserId(userId);
        List<FriendVO> result = new ArrayList<>();

        for (Friend friend : friends) {
            User friendUser = userService.getById(friend.getFriendId());
            if (friendUser == null) continue;

            FriendVO vo = new FriendVO();
            vo.setUserId(friendUser.getId());
            vo.setNickname(friendUser.getNickname());
            vo.setAvatar(friendUser.getAvatar());
            vo.setRemark(friend.getRemark());
            vo.setCreateTime(friend.getCreateTime());

            result.add(vo);
        }

        return result;
    }

    @Override
    @Transactional
    public boolean removeFriend(Long userId, Long friendId) {
        int result = friendMapper.deleteFriendship(userId, friendId);
        return result > 0;
    }

    @Override
    public boolean updateRemark(Long userId, Long friendId, String remark) {
        Friend friend = lambdaQuery()
                .eq(Friend::getUserId, userId)
                .eq(Friend::getFriendId, friendId)
                .eq(Friend::getStatus, 1)
                .one();

        if (friend == null) {
            return false;
        }

        friend.setRemark(remark);
        friend.setUpdateTime(new java.util.Date());
        return updateById(friend);
    }

    @Override
    public boolean isFriend(Long userId, Long friendId) {
        return friendMapper.existsFriendship(userId, friendId);
    }

    @Override
    @Transactional
    public void addFriendRelation(Long userId, Long friendId) {
        Friend friend1 = new Friend();
        friend1.setUserId(userId);
        friend1.setFriendId(friendId);
        friend1.setStatus(1);
        friend1.setCreateTime(new java.util.Date());
        friend1.setUpdateTime(new java.util.Date());
        save(friend1);

        Friend friend2 = new Friend();
        friend2.setUserId(friendId);
        friend2.setFriendId(userId);
        friend2.setStatus(1);
        friend2.setCreateTime(new java.util.Date());
        friend2.setUpdateTime(new java.util.Date());
        save(friend2);
    }
}
