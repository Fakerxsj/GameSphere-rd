package com.xsj.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName game_tag_relation
 */
@TableName(value ="game_tag_relation")
@Data
public class GameTagRelation {
    private Long id;

    private Long gameId;

    private Long tagId;

    private Date createTime;
}