package com.xsj.dto.request;

import lombok.Data;

@Data
public class GameSearchRequest {

    private String keyword;

    private String category;

    private String platform;

    private Double minRating;

    private Integer pageNum = 1;

    private Integer pageSize = 20;

    private String sortBy = "createTime";

    private String sortOrder = "desc";
}
