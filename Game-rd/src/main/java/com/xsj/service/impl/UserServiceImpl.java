package com.xsj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsj.entity.User;
import com.xsj.service.UserService;
import com.xsj.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService{

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLastLogin(Long userId, String ip) {
        User user = getById(userId);
        if (user != null) {
            user.setLastLoginTime(new java.util.Date());
            user.setLastLoginIp(ip);
            updateById(user);
        }
    }
}
