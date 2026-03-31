package com.xsj.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName comment
 */
@TableName(value ="comment")
@Data
public class Comment {
    private Long id;

    private Long userId;

    private Long gameId;

    private Long parentId;

    private String content;

    private String images;

    private Integer likeCount;

    private Integer replyCount;

    private Integer status;

    private Integer isTop;

    private Date createTime;

    private Date updateTime;
}