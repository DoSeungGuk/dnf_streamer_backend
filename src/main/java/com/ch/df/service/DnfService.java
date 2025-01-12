package com.ch.df.service;

import com.ch.df.dao.CharacterDAO;
import com.ch.df.dao.StreamerDAO;
import com.ch.df.entity.GameCharacter;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class DnfService {
    private final StreamerDAO streamerDAO;
    private final CharacterDAO characterDAO;
    private final TimelineService timelineService;

    public DnfService(StreamerDAO streamerDAO, CharacterDAO characterDAO, TimelineService timelineService) {
        this.streamerDAO = streamerDAO;
        this.characterDAO = characterDAO;
        this.timelineService = timelineService;
    }

    public Mono<Void> saveMatchingCharactersAsync(List<Map<String, Object>> characterDetails, LocalDateTime lastChecked) {
        return Flux.fromIterable(characterDetails)
                .flatMap(character -> {
                    String adventureName = (String) character.get("adventureName");
                    String serverId = (String) character.get("serverId");
                    String characterId = (String) character.get("characterId");

                    LocalDateTime start = LocalDateTime.parse("2025-01-01" + "T00:00:00");
                    LocalDateTime end = LocalDateTime.now();
                    LocalDateTime lastCheckedDate = start.minusDays(1); // 마지막 조회 날짜
                    System.out.println("serverId = " + serverId);
                    System.out.println("characterId = " + characterId);
                    // 스트리머 ID 조회
                    Integer streamerId = streamerDAO.findStreamerIdByAdventureNameAndServerId(adventureName, serverId);
                    if (streamerId != null) {
                        return timelineService.fetchTimeline(serverId, characterId, start, end, lastCheckedDate)
                                .flatMap(taechoCount -> {
                                    // 디버깅: 태초 아이템 횟수 확인
                                    System.out.println("Fetched Taecho Count for characterId " + characterId + ": " + taechoCount);

                                    // 캐릭터 정보 저장
                                    GameCharacter newCharacter = new GameCharacter();
                                    newCharacter.setServerId(serverId);
                                    newCharacter.setCharacterId(characterId);
                                    newCharacter.setCharacterName((String) character.get("characterName"));
                                    newCharacter.setLevel((Integer) character.get("level"));
                                    newCharacter.setJobName((String) character.get("jobName"));
                                    newCharacter.setJobGrowName((String) character.get("jobGrowName"));
                                    newCharacter.setFame((Integer) character.getOrDefault("fame", 0));
                                    newCharacter.setAdventureName(adventureName);
                                    newCharacter.setGuildName((String) character.get("guildName"));
                                    newCharacter.setStreamerId(streamerId);

                                    // 태초 수 설정
                                    newCharacter.setTaechoCount(taechoCount);
                                    newCharacter.setLastChecked(lastChecked);

                                    // 디버깅: 저장 전 객체 확인
                                    System.out.println("Saving GameCharacter: " + newCharacter);

                                    // DB 저장
                                    characterDAO.insertOrUpdateCharacter(newCharacter);

                                    return Mono.empty();
                                });

                    } else {
                        System.out.println("Streamer not found for adventureName: " + adventureName);
                        return Mono.empty();
                    }
                })
                .then();
    }}
