package com.xsj.mapper;

import com.xsj.entity.SystemLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 28227
* @description 针对表【system_log(系统日志表)】的数据库操作Mapper
* @createDate 2026-03-31 10:06:07
* @Entity com.xsj.pojo.SystemLog
*/
@Mapper
public interface SystemLogMapper extends BaseMapper<SystemLog> {

}




