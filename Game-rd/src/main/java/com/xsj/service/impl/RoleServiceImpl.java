package com.xsj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsj.entity.Role;
import com.xsj.service.RoleService;
import com.xsj.mapper.RoleMapper;
import org.springframework.stereotype.Service;

/**
* @author 28227
* @description 针对表【role(角色表)】的数据库操作Service实现
* @createDate 2026-03-31 10:06:07
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
    implements RoleService{

}




