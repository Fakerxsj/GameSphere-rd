package com.xsj.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "会话列表响应")
public class ConversationVO {

    @Schema(description = "对方用户ID")
    private Long userId;

    @Schema(description = "对方昵称")
    private String nickname;

    @Schema(description = "对方头像")
    private String avatar;

    @Schema(description = "最后一条消息内容")
    private String lastMessage;

    @Schema(description = "最后消息时间")
    private Date lastMessageTime;

    @Schema(description = "未读消息数")
    private Integer unreadCount;
}