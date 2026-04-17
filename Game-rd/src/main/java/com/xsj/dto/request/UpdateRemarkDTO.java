package com.xsj.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "更新好友备注请求")
public class UpdateRemarkDTO {

    @NotBlank(message = "备注名不能为空")
    @Schema(description = "备注名", maxLength = 50)
    private String remark;
}
