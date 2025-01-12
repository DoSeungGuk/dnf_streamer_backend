package com.ch.df.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class StreamerRankingService {
    private final TimelineService timelineService;

    public StreamerRankingService(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    // 스트리머별 "태초 아이템" 순위 계산
    public Mono<Map<String, Integer>> calculateStreamerRanking(List<String> streamerIds, LocalDateTime lastCheckedDate) {
        return Flux.fromIterable(streamerIds)
                .flatMap(streamerId -> {
                    // 스트리머의 모든 캐릭터 조회 및 카운트
                    List<String> characterIds = fetchStreamerCharacters(streamerId);
                    return Flux.fromIterable(characterIds)
                            .flatMap(characterId -> timelineService.fetchTimeline("serverId", characterId, lastCheckedDate.minusDays(90), LocalDateTime.now(), lastCheckedDate))
                            .reduce(Integer::sum) // 캐릭터별 "태초 아이템" 합산
                            .map(totalCount -> Map.entry(streamerId, totalCount)); // 스트리머 ID와 총 개수의 Map.Entry 반환
                })
                .collectMap(Map.Entry::getKey, Map.Entry::getValue); // Map.Entry를 Map으로 변환
    }

    private List<String> fetchStreamerCharacters(String streamerId) {
        // DB나 API에서 해당 스트리머의 캐릭터 목록을 조회
        return List.of("characterId1", "characterId2", "characterId3");
    }
}


