package com.xsj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsj.dto.response.FriendRequestVO;
import com.xsj.entity.FriendRequest;
import com.xsj.entity.User;
import com.xsj.mapper.FriendRequestMapper;
import com.xsj.service.FriendRequestService;
import com.xsj.service.FriendService;
import com.xsj.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendRequestServiceImpl extends ServiceImpl<FriendRequestMapper, FriendRequest> implements FriendRequestService {

    private final FriendRequestMapper friendRequestMapper;
    private final UserService userService;
    private final FriendService friendService;

    @Override
    public boolean sendRequest(Long fromUserId, Long toUserId, String message, Long sourceCommentId) {
        if (fromUserId.equals(toUserId)) {
            throw new RuntimeException("不能添加自己为好友");
        }

        if (friendService.isFriend(fromUserId, toUserId)) {
            throw new RuntimeException("你们已经是好友了");
        }

        if (hasPendingRequest(fromUserId, toUserId)) {
            throw new RuntimeException("已发送过好友申请，请等待对方处理");
        }

        FriendRequest request = new FriendRequest();
        request.setFromUserId(fromUserId);
        request.setToUserId(toUserId);
        request.setMessage(message);
        request.setStatus(0);
        request.setSourceCommentId(sourceCommentId);
        request.setCreateTime(new java.util.Date());
        request.setUpdateTime(new java.util.Date());

        return save(request);
    }

    @Override
    @Transactional
    public boolean handleRequest(Long requestId, Long userId, Integer status) {
        FriendRequest request = getById(requestId);
        if (request == null) {
            throw new RuntimeException("好友申请不存在");
        }

        if (!request.getToUserId().equals(userId)) {
            throw new RuntimeException("无权处理该申请");
        }

        if (request.getStatus() != 0) {
            throw new RuntimeException("该申请已处理");
        }

        request.setStatus(status);
        request.setUpdateTime(new java.util.Date());
        updateById(request);

        if (status == 1) {
            friendService.addFriendRelation(request.getFromUserId(), request.getToUserId());
        }

        return true;
    }

    @Override
    public List<FriendRequestVO> getReceivedRequests(Long userId) {
        List<FriendRequest> requests = lambdaQuery()
                .eq(FriendRequest::getToUserId, userId)
                .eq(FriendRequest::getStatus, 0)
                .orderByDesc(FriendRequest::getCreateTime)
                .list();

        List<FriendRequestVO> result = new ArrayList<>();
        for (FriendRequest request : requests) {
            User fromUser = userService.getById(request.getFromUserId());
            if (fromUser == null) continue;

            FriendRequestVO vo = new FriendRequestVO();
            vo.setId(request.getId());
            vo.setFromUserId(fromUser.getId());
            vo.setFromUserNickname(fromUser.getNickname());
            vo.setFromUserAvatar(fromUser.getAvatar());
            vo.setMessage(request.getMessage());
            vo.setStatus(request.getStatus());
            vo.setSourceCommentId(request.getSourceCommentId());
            vo.setCreateTime(request.getCreateTime());

            result.add(vo);
        }

        return result;
    }

    @Override
    public boolean hasPendingRequest(Long fromUserId, Long toUserId) {
        return lambdaQuery()
                .eq(FriendRequest::getFromUserId, fromUserId)
                .eq(FriendRequest::getToUserId, toUserId)
                .eq(FriendRequest::getStatus, 0)
                .count() > 0;
    }
}
