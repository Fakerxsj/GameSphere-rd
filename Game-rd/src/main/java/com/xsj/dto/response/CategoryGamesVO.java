package com.xsj.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class CategoryGamesVO {
    private Long categoryId;
    private String categoryName;
    private List<GameBriefVO> games;
}
