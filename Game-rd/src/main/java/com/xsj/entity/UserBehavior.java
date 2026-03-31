package com.xsj.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName user_behavior
 */
@TableName(value ="user_behavior")
@Data
public class UserBehavior {
    @TableId
    private Long id;

    private Long userId;

    private Long gameId;

    private String behaviorType;

    private Date behaviorTime;

    private Integer duration;

    private String device;

    private String ipAddress;

    private String extraData;

    private Date createTime;
}