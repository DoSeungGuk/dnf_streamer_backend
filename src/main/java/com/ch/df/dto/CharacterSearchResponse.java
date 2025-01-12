package com.ch.df.dto;

import lombok.Data;

import java.util.List;

@Data
public class CharacterSearchResponse {
    private List<Character> rows;

    @Data
    public static class Character {
        private String characterId;
        private String characterName;
        private String adventureName;
        private String serverId;
    }
}
