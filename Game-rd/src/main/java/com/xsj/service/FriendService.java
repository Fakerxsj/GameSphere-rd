package com.xsj.service;

import com.xsj.dto.response.FriendVO;
import com.xsj.entity.Friend;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface FriendService extends IService<Friend> {

    List<FriendVO> getFriendList(Long userId);

    boolean removeFriend(Long userId, Long friendId);

    boolean updateRemark(Long userId, Long friendId, String remark);

    boolean isFriend(Long userId, Long friendId);

    void addFriendRelation(Long userId, Long friendId);
}
