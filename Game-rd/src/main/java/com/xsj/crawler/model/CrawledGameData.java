package com.xsj.crawler.model;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class CrawledGameData {

    private String name;
    private String englishName;
    private String alias;

    private String coverImageUrl;
    private String bannerImageUrl;
    private List<String> screenshotUrls;

    private String gameType;
    private String developer;
    private String publisher;

    private Date releaseDate;
    private String platform;

    private BigDecimal price;
    private BigDecimal discount;

    private Double ratingScore;
    private Integer ratingCount;
    private Integer followCount;

    private String description;
    private String officialWebsite;

    private String sourceUrl;
    private String sourceSite;

    private List<String> categories;
    private List<String> tags;

    private Date crawlTime;
}
