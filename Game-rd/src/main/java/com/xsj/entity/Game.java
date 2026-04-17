package com.xsj.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @TableName game
 */
@TableName(value ="game")
@Data
public class Game {
    @TableId
    private Long id;

    private Long igdbId;

    private String name;

    private String englishName;

    private String alias;

    private String coverImage;

    private String bannerImage;

    private String screenshots;

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

    private Integer commentCount;

    private String description;

    private String officialWebsite;

    private String videoUrl;

    private String trailerUrl;

    private String sourceUrl;

    private String sourceSite;

    private Integer status;

    private Integer isRecommend;

    private Integer recommendWeight;



    private Date createTime;

    private Date updateTime;
}