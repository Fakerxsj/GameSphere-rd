package com.xsj.dto.response;

import lombok.Data;

@Data
public class FourmSectionVO {

    private Long id;

    private Long gameId;

    private String name;

    private String icon;

    private Integer postCount;

    private Integer orderNum;
}
