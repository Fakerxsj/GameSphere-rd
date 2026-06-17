package com.xsj.dto.response;

// ... existing code ...
import lombok.Data;
import java.math.BigDecimal;

@Data
public class GameListResponse {

    private Long id;

    private String name;

    private String coverImage;

    private String videoUrl;

    private String trailerUrl;

    private String gameType;

    private String platform;

    private BigDecimal price;

    private BigDecimal discount;

    private BigDecimal ratingScore;

    private Integer ratingCount;

    private Integer followCount;

    private Integer downloadCount;

    private Integer commentCount;

    private String briefDescription;

    private String developer;

    private String publisher;

    private String description;
}
