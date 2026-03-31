package com.xsj.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName recommendation_record
 */
@TableName(value ="recommendation_record")
@Data
public class RecommendationRecord {
    private Long id;

    private Long userId;

    private String algorithm;

    private String gameIds;

    private String scoreMatrix;

    private String reason;

    private Date createTime;
}