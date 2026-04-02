package com.xsj.recommend.model;

import lombok.Data;
import java.util.Map;

@Data
public class UserPreferenceModel {

    private Long userId;

    private Map<String, Long> categoryPreferences;

    private Map<String, Long> tagPreferences;

    private Map<String, Long> platformPreferences;

    private Map<Double, Long> priceRangePreferences;

    private Long viewScore;

    private Long collectScore;

    private Long downloadScore;

    public Double getTotalScore() {
        return (double) (viewScore != null ? viewScore : 0) +
                (collectScore != null ? collectScore : 0) * 5 +
                (downloadScore != null ? downloadScore : 0) * 10;
    }
}
