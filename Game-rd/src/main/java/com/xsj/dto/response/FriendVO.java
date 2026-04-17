package com.xsj.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "好友信息响应")
public class FriendVO {

    @Schema(description = "好友用户ID")
    private Long userId;

    @Schema(description = "好友昵称")
    private String nickname;

    @Schema(description = "好友头像")
    private String avatar;

    @Schema(description = "备注名")
    private String remark;

    @Schema(description = "最后消息时间")
    private Date lastMessageTime;

    @Schema(description = "最后一条消息")
    private String lastMessage;

    @Schema(description = "未读消息数")
    private Integer unreadCount;

    @Schema(description = "好友关系创建时间")
    private Date createTime;
}