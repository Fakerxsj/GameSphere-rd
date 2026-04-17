package com.xsj.service;

import com.xsj.dto.response.FriendRequestVO;
import com.xsj.entity.FriendRequest;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface FriendRequestService extends IService<FriendRequest> {

    boolean sendRequest(Long fromUserId, Long toUserId, String message, Long sourceCommentId);

    boolean handleRequest(Long requestId, Long userId, Integer status);

    List<FriendRequestVO> getReceivedRequests(Long userId);

    boolean hasPendingRequest(Long fromUserId, Long toUserId);
}
