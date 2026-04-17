package com.xsj.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "发送消息请求")
public class SendMessageDTO {

    @NotNull(message = "接收者ID不能为空")
    @Schema(description = "接收者ID")
    private Long toUserId;

    @NotBlank(message = "消息内容不能为空")
    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "消息类型:1-文本,2-图片,3-系统消息", defaultValue = "1")
    private Integer messageType = 1;
}