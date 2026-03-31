package com.xsj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsj.entity.User;
import com.xsj.service.UserService;
import com.xsj.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 28227
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2026-03-31 10:06:07
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




