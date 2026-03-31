package com.xsj.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName game_category
 */
@TableName(value ="game_category")
@Data
public class GameCategory {
    private Long id;

    private Long gameId;

    private Long categoryId;

    private Date createTime;
}