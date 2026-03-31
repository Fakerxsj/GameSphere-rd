package com.xsj.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName role
 */
@TableName(value ="role")
@Data
public class Role {
    private Long id;

    private String roleName;

    private String roleCode;

    private String description;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}