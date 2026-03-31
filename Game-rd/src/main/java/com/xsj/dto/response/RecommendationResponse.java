package com.xsj.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class RecommendationResponse {

    private List<GameListResponse> games;

    private String algorithm;

    private String reason;

    private Integer total;
}
