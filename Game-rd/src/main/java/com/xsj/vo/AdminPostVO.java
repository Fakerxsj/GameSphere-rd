package com.xsj.vo;

import lombok.Data;
import java.util.Date;

@Data
public class AdminPostVO {
    private Long id;
    private Long gameId;
    private String gameName;
    private String userNickname;
    private String content;
    private Integer likeCount;
    private Integer replyCount;
    private Date createTime;
}
