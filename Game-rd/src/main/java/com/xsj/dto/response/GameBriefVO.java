package com.xsj.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class GameBriefVO {
    private Long id;
    private String name;
    private String coverImage;
    private String bannerImage;
    private Integer commentCount;
    private Integer followCount;
    private BigDecimal ratingScore;
    private String gameType;
    private String developer;
}
