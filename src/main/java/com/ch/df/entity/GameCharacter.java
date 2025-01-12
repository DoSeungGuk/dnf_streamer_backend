package com.ch.df.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
public class GameCharacter {
    private String serverId;
    private String characterId;
    private String characterName;
    private Integer level;
    private String jobName;
    private String jobGrowName;
    private Integer fame;
    private String adventureName;
    private String guildName;
    private Integer streamerId;
    private Integer taechoCount;          // 태초 아이템 획득 수
    private LocalDateTime lastChecked;   // 최근 조회 날짜
}
