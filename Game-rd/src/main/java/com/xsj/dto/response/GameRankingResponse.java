package com.xsj.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class GameRankingResponse {
    private Long id;
    private String name;
    private String coverImage;
    private String videoUrl;
    private BigDecimal ratingScore;
    private Integer ratingCount;
    private Integer downloadCount;
    private Integer followCount;
    private String developer;
    private String platform;
    private String gameType;
    private String description;
    private Date releaseDate;
}
