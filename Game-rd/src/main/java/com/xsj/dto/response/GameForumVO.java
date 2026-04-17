package com.xsj.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class GameForumVO {

    private Long gameId;

    private String gameName;

    private String coverImage;

    private String backgroundImage;

    private Integer followCount;

    private Integer postCount;

    private Boolean isFollowed;

    private List<FourmSectionVO> sections;

    @Data
    public static class FollowedAndHotData {
        private List<GameBriefVO> followedForums;
        private List<GameBriefVO> hotForums;
    }
}
