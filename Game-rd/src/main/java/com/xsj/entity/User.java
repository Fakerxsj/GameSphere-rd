package com.xsj.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User {
    @TableId
    private Long id;

    private String username;

    private String password;

    private String email;

    private String phone;

    private String nickname;

    private String avatar;

    private Integer gender;

    private Date birthday;

    private String signature;

    private Long roleId;

    private Integer status;

    private Date lastLoginTime;

    private String lastLoginIp;

    private Date createTime;

    private Date updateTime;
}