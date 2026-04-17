package com.xsj.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "消息响应")
public class MessageVO {

    @Schema(description = "消息ID")
    private Long id;

    @Schema(description = "发送者ID")
    private Long fromUserId;

    @Schema(description = "发送者昵称")
    private String fromUserNickname;

    @Schema(description = "发送者头像")
    private String fromUserAvatar;

    @Schema(description = "接收者ID")
    private Long toUserId;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "消息类型:1-文本,2-图片,3-系统消息")
    private Integer messageType;

    @Schema(description = "是否已读:0-未读,1-已读")
    private Integer isRead;

    @Schema(description = "发送时间")
    private Date createTime;
}