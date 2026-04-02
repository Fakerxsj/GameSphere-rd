package com.xsj.recommend.model;

import lombok.Data;

@Data
public class GameSimilarityModel {

    private Long game1Id;

    private Long game2Id;

    private Double similarityScore;

    private String similarityType;

    public GameSimilarityModel() {
    }

    public GameSimilarityModel(Long game1Id, Long game2Id, Double similarityScore, String similarityType) {
        this.game1Id = game1Id;
        this.game2Id = game2Id;
        this.similarityScore = similarityScore;
        this.similarityType = similarityType;
    }
}
