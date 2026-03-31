package com.xsj.vo;

import lombok.Data;

@Data
public class UserVO {

    private Long id;

    private String username;

    private String nickname;

    private String avatar;

    private String signature;

    private Integer followCount;

    private Integer fanCount;

    private Integer gameCount;
}
