package com.xsj.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName system_log
 */
@TableName(value ="system_log")
@Data
public class SystemLog {
    private Long id;

    private Long userId;

    private String module;

    private String operation;

    private String method;

    private String params;

    private String result;

    private String ipAddress;

    private String userAgent;

    private Long executeTime;

    private Integer status;

    private String errorMsg;

    private Date createTime;
}