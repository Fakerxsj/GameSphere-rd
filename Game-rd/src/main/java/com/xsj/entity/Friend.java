package com.xsj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName friend
 */
@TableName(value ="friend")
@Data
public class Friend {
    private Long id;

    private Long userId;

    private Long friendId;

    private String remark;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}