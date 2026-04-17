package com.xsj.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "处理好友申请请求")
public class HandleFriendRequestDTO {

    @NotNull(message = "申请ID不能为空")
    @Schema(description = "好友申请ID")
    private Long requestId;

    @NotNull(message = "处理状态不能为空")
    @Schema(description = "处理状态:1-同意,2-拒绝")
    private Integer status;
}