package com.xsj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsj.entity.UserFollow;
import com.xsj.service.UserFollowService;
import com.xsj.mapper.UserFollowMapper;
import org.springframework.stereotype.Service;

/**
* @author 28227
* @description 针对表【user_follow(用户关注表)】的数据库操作Service实现
* @createDate 2026-03-31 10:06:07
*/
@Service
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollow>
    implements UserFollowService{

}




