package com.xsj.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "好友申请响应")
public class FriendRequestVO {

    @Schema(description = "申请ID")
    private Long id;

    @Schema(description = "申请人ID")
    private Long fromUserId;

    @Schema(description = "申请人昵称")
    private String fromUserNickname;

    @Schema(description = "申请人头像")
    private String fromUserAvatar;

    @Schema(description = "申请消息")
    private String message;

    @Schema(description = "状态:0-待处理,1-已同意,2-已拒绝")
    private Integer status;

    @Schema(description = "来源评论ID")
    private Long sourceCommentId;

    @Schema(description = "申请时间")
    private Date createTime;
}