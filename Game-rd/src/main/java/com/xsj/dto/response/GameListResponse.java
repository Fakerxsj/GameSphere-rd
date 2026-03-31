package com.xsj.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class GameListResponse {

    private Long id;

    private String name;

    private String coverImage;

    private String gameType;

    private String platform;

    private BigDecimal price;

    private BigDecimal discount;

    private BigDecimal ratingScore;

    private Integer ratingCount;

    private Integer followCount;

    private String briefDescription;
}
