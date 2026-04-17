package com.xsj.service;

import com.xsj.dto.response.FourmSectionVO;
import com.xsj.dto.response.GameBriefVO;
import com.xsj.dto.response.GameForumVO;

import java.util.List;

public interface ForumService {

    List<GameBriefVO> getFollowedForums(Long userId);

    List<GameBriefVO> getHotForums(Integer limit);

    GameForumVO getGameForumInfo(Long gameId, Long userId);

    List<FourmSectionVO> getForumSections(Long gameId);
}
