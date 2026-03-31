package com.xsj.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @TableName rating
 */
@TableName(value ="rating")
@Data
public class Rating {
    private Long id;

    private Long userId;

    private Long gameId;

    private BigDecimal score;

    private String content;

    private Integer upvoteCount;

    private Integer downvoteCount;

    private Integer isOfficial;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}