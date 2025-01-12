package com.ch.df.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Character {
    private int id;
    private int streamerId;
    private String characterId;
    private String serverId;
    private int fame;
}
