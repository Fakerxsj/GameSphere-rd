package com.xsj.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class GameDetailResponse {

    private Long id;

    private String name;

    private String englishName;

    private String alias;

    private String coverImage;

    private String bannerImage;

    private List<String> screenshots;

    private String gameType;

    private String developer;

    private String publisher;

    private Date releaseDate;

    private String platform;

    private BigDecimal price;

    private BigDecimal discount;

    private BigDecimal ratingScore;

    private Integer ratingCount;

    private Integer followCount;

    private Integer downloadCount;

    private String description;

    private String officialWebsite;

    private List<String> categories;

    private List<String> tags;

    private Integer status;

    private Integer isRecommend;
}
