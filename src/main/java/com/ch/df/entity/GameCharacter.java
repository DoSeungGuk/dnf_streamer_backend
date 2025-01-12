package com.ch.df.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
}
