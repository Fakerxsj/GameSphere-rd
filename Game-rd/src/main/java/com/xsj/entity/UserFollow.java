package com.xsj.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName user_follow
 */
@TableName(value ="user_follow")
@Data
public class UserFollow {
    private Long id;

    private Long userId;

    private String followType;

    private Long followTargetId;

    private Date createTime;
}