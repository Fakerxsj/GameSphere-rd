package com.xsj.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequest {

    @NotNull(message = "游戏 ID 不能为空")
    private Long gameId;

    @NotBlank(message = "评论内容不能为空")
    private String content;

    private Long parentId = 0L;

    private String images;
}
