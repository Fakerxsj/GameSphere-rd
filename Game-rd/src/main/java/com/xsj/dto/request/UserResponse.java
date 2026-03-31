package com.xsj.dto.response;

import lombok.Data;
import java.util.Date;

@Data
public class UserResponse {

    private Long id;

    private String username;

    private String email;

    private String phone;

    private String nickname;

    private String avatar;

    private Integer gender;

    private String signature;

    private Date lastLoginTime;

    private Date createTime;
}
