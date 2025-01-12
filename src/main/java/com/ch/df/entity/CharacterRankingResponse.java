package com.ch.df.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CharacterRankingResponse {
    private String characterName;
    private Integer taechoCount;
    private Integer fame;
    private String jobGrowName;
    private String streamerName; // 새 필드 추가
}
