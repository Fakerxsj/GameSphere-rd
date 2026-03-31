package com.xsj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsj.entity.SystemLog;
import com.xsj.service.SystemLogService;
import com.xsj.mapper.SystemLogMapper;
import org.springframework.stereotype.Service;

/**
* @author 28227
* @description 针对表【system_log(系统日志表)】的数据库操作Service实现
* @createDate 2026-03-31 10:06:07
*/
@Service
public class SystemLogServiceImpl extends ServiceImpl<SystemLogMapper, SystemLog>
    implements SystemLogService{

}




