package com.xsj.service;

import com.xsj.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
* @author 28227
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2026-03-31 10:06:07
*/
public interface UserService extends IService<User> {

    @Transactional(rollbackFor = Exception.class)
    void updateLastLogin(Long userId, String ip);
}
