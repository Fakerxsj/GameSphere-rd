package com.xsj.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class CommentVO {

    private Long id;

    private Long userId;

    private String userNickname;

    private String userAvatar;

    private Long gameId;

    private String content;

    private List<String> images;

    private Integer likeCount;

    private Integer replyCount;

    private Boolean isLiked;

    private Date createTime;

    private List<CommentVO> replies;
}
