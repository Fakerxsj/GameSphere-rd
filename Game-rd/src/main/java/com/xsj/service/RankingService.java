package com.xsj.service;

import com.xsj.dto.response.GameRankingResponse;
import java.util.List;

public interface RankingService {
    List<GameRankingResponse> getHotRanking(Integer limit, Integer offset);
    List<GameRankingResponse> getRatingRanking(Integer limit, Integer offset);
    List<GameRankingResponse> getDownloadRanking(Integer limit, Integer offset);
    List<GameRankingResponse> getFollowRanking(Integer limit, Integer offset);
}
