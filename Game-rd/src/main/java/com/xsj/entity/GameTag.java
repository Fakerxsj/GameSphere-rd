package com.xsj.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName game_tag
 */
@TableName(value ="game_tag")
@Data
public class GameTag {
    private Long id;

    private String name;

    private String color;

    private Integer sortOrder;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}