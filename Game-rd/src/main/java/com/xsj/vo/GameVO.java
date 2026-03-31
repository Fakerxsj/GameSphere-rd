package com.xsj.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class GameVO {

    private Long id;

    private String name;

    private String coverImage;

    private String bannerImage;

    private String gameType;

    private String platform;

    private BigDecimal price;

    private BigDecimal discount;

    private BigDecimal ratingScore;

    private Integer ratingCount;

    private Integer followCount;

    private List<String> categories;

    private List<String> tags;

    private Boolean isCollected;

    private Boolean isFollowed;
}
