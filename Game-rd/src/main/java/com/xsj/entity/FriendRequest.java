package com.xsj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName friend_request
 */
@TableName(value ="friend_request")
@Data
public class FriendRequest {
    private Long id;

    private Long fromUserId;

    private Long toUserId;

    private String message;

    private Integer status;

    private Long sourceCommentId;

    private Date createTime;

    private Date updateTime;
}