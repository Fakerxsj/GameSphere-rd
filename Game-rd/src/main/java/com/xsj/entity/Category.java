package com.xsj.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName category
 */
@TableName(value ="category")
@Data
public class Category {
    private Long id;

    private String name;

    private Long parentId;

    private Integer level;

    private String icon;

    private Integer sortOrder;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}