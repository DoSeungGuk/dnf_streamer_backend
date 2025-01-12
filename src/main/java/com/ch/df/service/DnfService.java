package com.ch.df.service;

import com.ch.df.dao.CharacterDAO;
import com.ch.df.dao.StreamerDAO;
import com.ch.df.entity.GameCharacter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DnfService {
    private final StreamerDAO streamerDAO;
    private final CharacterDAO characterDAO;

    public DnfService(StreamerDAO streamerDAO, CharacterDAO characterDAO) {
        this.streamerDAO = streamerDAO;
        this.characterDAO = characterDAO;
    }

    public void saveMatchingCharacters(List<Map<String, Object>> characterDetails) {
        for (Map<String, Object> character : characterDetails) {
            String adventureName = (String) character.get("adventureName");
            String serverId = (String) character.get("serverId");

            // 스트리머 ID 조회
            Integer streamerId = streamerDAO.findStreamerIdByAdventureNameAndServerId(adventureName, serverId);
            if (streamerId != null) {
                // 캐릭터 정보 저장
                GameCharacter newCharacter = new GameCharacter();
                newCharacter.setServerId((String) character.get("serverId"));
                newCharacter.setCharacterId((String) character.get("characterId"));
                newCharacter.setCharacterName((String) character.get("characterName"));
                newCharacter.setLevel((Integer) character.get("level"));
                newCharacter.setJobName((String) character.get("jobName"));
                newCharacter.setJobGrowName((String) character.get("jobGrowName"));
                newCharacter.setFame((Integer) character.getOrDefault("fame", 0));
                newCharacter.setAdventureName(adventureName);
                newCharacter.setGuildName((String) character.get("guildName"));
                newCharacter.setStreamerId(streamerId);

                characterDAO.insertOrUpdateCharacter(newCharacter);
            } else {
                System.out.println("Streamer not found for adventureName: " + adventureName);
            }
        }
    }
}
