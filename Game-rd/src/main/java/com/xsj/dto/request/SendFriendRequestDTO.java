package com.xsj.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "发送好友申请请求")
public class SendFriendRequestDTO {

    @NotNull(message = "目标用户ID不能为空")
    @Schema(description = "目标用户ID")
    private Long toUserId;

    @Schema(description = "申请消息", maxLength = 200)
    private String message;

    @Schema(description = "来源评论ID")
    private Long sourceCommentId;
}
