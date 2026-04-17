package com.xsj.dto.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ForumPostVO {

    private Long id;

    private Long gameId;

    private Long userId;

    private String userNickname;

    private String userAvatar;

    private String title;

    private String content;

    private List<String> images;

    private Integer likeCount;

    private Integer replyCount;

    private Integer viewCount;

    private Boolean isTop;

    private Boolean isEssence;

    private Boolean isOfficial;

    private Boolean isLiked;

    private String sectionName;

    private List<String> tags;

    private Date createTime;

    private Date updateTime;
}
