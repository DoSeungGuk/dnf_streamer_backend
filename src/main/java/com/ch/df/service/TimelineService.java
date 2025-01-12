package com.ch.df.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TimelineService {
    // 태초 아이템 목록
    private static final Set<String> TAECHO_ITEMS = Set.of(
            // 무기
            "태초의 별 - 소검", "무형검 - 생사경", "태초의 별 - 도", "역작 금인필살도",
            "태초의 별 - 둔기", "양얼의 나뭇가지 : 초", "태초의 별 - 대검",
            "특형 - 호위무신의 운검", "태초의 별 - 광검", "멸룡검 발뭉",
            "태초의 별 - 너클", "넨 아스트론", "태초의 별 - 건틀릿",
            "미완성 코스믹 건틀릿", "태초의 별 - 클로", "리리스, 디 이블",
            "태초의 별 - 권투글러브", "엠프레스 벳즈", "태초의 별 - 통파",
            "파운더 오브 마나", "태초의 별 - 리볼버", "노블레스 오브 레인저",
            "태초의 별 - 자동권총", "에볼루션 오토매틱 건", "태초의 별 - 머스켓",
            "리턴드 스나이퍼 오브 블랙 로즈", "태초의 별 - 핸드캐넌",
            "우르반의 걸작", "태초의 별 - 보우건", "얼어붙은 불꽃의 살",
            "태초의 별 - 창", "앱솔루트 제로", "태초의 별 - 봉", "하쿠나마타타 : 원더풀",
            "태초의 별 - 로드", "양치기의 마지막 진실", "태초의 별 - 스태프",
            "해방된 지식", "태초의 별 - 빗자루", "칠흑의 저주", "태초의 별 - 십자가",
            "영원한 올리브 나무 십자가", "태초의 별 - 염주", "폴링 스타 로저리",
            "태초의 별 - 토템", "폭군의 본의", "태초의 별 - 낫", "소울 프레데터",
            "태초의 별 - 배틀액스", "멸망의 근원", "태초의 별 - 단검", "여제의 영롱한 은장도",
            "태초의 별 - 쌍검", "샤이닝 인페르노", "태초의 별 - 완드", "로드 오브 호러",
            "태초의 별 - 차크라 웨펀", "육도의 수레바퀴", "태초의 별 - 장창", "전장의 투신",
            "태초의 별 - 미늘창", "眞 : 흑룡언월도", "태초의 별 - 광창", "성장군 : 유성계",
            "태초의 별 - 투창", "무영흑단살", "태초의 별 - 장도", "만월 : 월광야천도",
            "태초의 별 - 소태도", "나스카 : 영혼의 심판", "태초의 별 - 중검", "Brutal-Saw",
            "태초의 별 - 코어 블레이드", "코어 오리진", "태초의 별 - 장궁",
            "미스트 파이오니어", "태초의 별 - 크로스슈터", "패밀리 팔케", "태초의 별 - 에테리얼 보우",
            "인요의 황혼",

            // 악세사리
            "칠흑같은 그림자 속 목걸이 - 혈", "로열 페어리 목걸이", "영롱한 황금향의 축복 - 목걸이",
            "용제의 공포를 심는 포효", "혼돈의 정화 목걸이", "견고한 행운이 깃든 목걸이",
            "과충전 - 한계를 여는 열쇠 목걸이", "자연 - 천재지변 목걸이", "발할라의 여신 발키리 천상 목걸이",
            "대 여우의 영혼 목걸이", "무리의 길잡이 목걸이", "넘치는 마력의 영역 목걸이",
            "흑아 : 칠흑같은 그림자 속 목걸이 - 혈", "흑아 : 로열 페어리 목걸이",
            "흑아 : 영롱한 황금향의 축복 - 목걸이", "흑아 : 용제의 공포를 심는 포효",
            "흑아 : 혼돈의 정화 목걸이", "흑아 : 견고한 행운이 깃든 목걸이",
            "흑아 : 과충전 - 한계를 여는 열쇠 목걸이", "흑아 : 자연 - 천재지변 목걸이",
            "흑아 : 발할라의 여신 발키리 천상 목걸이", "흑아 : 대 여우의 영혼 목걸이",
            "흑아 : 무리의 길잡이 목걸이", "흑아 : 넘치는 마력의 영역 목걸이",

            // 팔찌
            "칠흑같은 그림자 속 팔찌 - 화", "로열 페어리 팔찌", "영롱한 황금향의 영광 - 팔찌",
            "용제의 피를 머금은 흉아", "혼돈의 정화 팔찌", "견고한 행운이 깃든 팔찌",
            "과충전 - 한계를 가리키는 지침 팔찌", "자연 - 천재지변 팔찌",
            "발할라의 여신 발키리 수호 팔찌", "대 여우의 지혜 팔찌", "무리의 길잡이 팔목 보호대",
            "넘치는 마력의 영역 팔찌", "흑아 : 칠흑같은 그림자 속 팔찌 - 화",
            "흑아 : 로열 페어리 팔찌", "흑아 : 영롱한 황금향의 영광 - 팔찌",
            "흑아 : 용제의 피를 머금은 흉아", "흑아 : 혼돈의 정화 팔찌",
            "흑아 : 견고한 행운이 깃든 팔찌", "흑아 : 과충전 - 한계를 가리키는 지침 팔찌",
            "흑아 : 자연 - 천재지변 팔찌", "흑아 : 발할라의 여신 발키리 수호 팔찌",
            "흑아 : 대 여우의 지혜 팔찌", "흑아 : 무리의 길잡이 팔목 보호대",
            "흑아 : 넘치는 마력의 영역 팔찌",

            // 반지
            "칠흑같은 그림자 속 반지 - 독", "로열 페어리 반지", "영롱한 황금향의 꿈 - 반지",
            "용제의 재해를 막는 비늘", "혼돈의 정화 반지", "견고한 행운이 깃든 반지",
            "과충전 - 한계를 돌파하는 균열 반지", "자연 - 천재지변 반지",
            "발할라의 여신 발키리 축복 반지", "대 여우의 매혹 반지", "무리의 길잡이 반지",
            "넘치는 마력의 영역 반지", "흑아 : 칠흑같은 그림자 속 반지 - 독",
            "흑아 : 로열 페어리 반지", "흑아 : 영롱한 황금향의 꿈 - 반지",
            "흑아 : 용제의 재해를 막는 비늘", "흑아 : 혼돈의 정화 반지",
            "흑아 : 견고한 행운이 깃든 반지", "흑아 : 과충전 - 한계를 돌파하는 균열 반지",
            "흑아 : 자연 - 천재지변 반지", "흑아 : 발할라의 여신 발키리 축복 반지",
            "흑아 : 대 여우의 매혹 반지", "흑아 : 무리의 길잡이 반지", "흑아 : 넘치는 마력의 영역 반지"
    );

    // 태초 아이템 여부 확인
    public boolean isTaechoItem(String itemName) {
        return TAECHO_ITEMS.contains(itemName);
    }

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${api.key}")
    private String apiKey;

    public TimelineService(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }
    /**
     * 캐릭터 타임라인을 조회하는 메서드
     * @param serverId 서버 ID
     * @param characterId 캐릭터 ID
     * @param startDate 조회 시작 날짜
     * @param endDate 조회 종료 날짜
     * @param lastCheckedDate 가장 최근 조회된 날짜
     * @return Mono<Integer> 태초 아이템 카운트
     */
    public Mono<Integer> fetchTimeline(String serverId, String characterId, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime lastCheckedDate) {
        // 기간을 90일 단위로 나눔
        long daysDiff = java.time.Duration.between(
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().atStartOfDay()
        ).toDays();
        int splitCount = (int) Math.ceil((double) daysDiff / 90);

        List<LocalDateTime> startDates = new ArrayList<>();
        List<LocalDateTime> endDates = new ArrayList<>();

        for (int i = 0; i < splitCount; i++) {
            LocalDateTime chunkStart = startDate.plusDays(i * 90L);
            LocalDateTime chunkEnd = chunkStart.plusDays(89).isBefore(endDate) ? chunkStart.plusDays(89) : endDate;

            startDates.add(chunkStart);
            endDates.add(chunkEnd);
        }

        // 90일 단위로 분리된 기간별로 호출
        return Flux.range(0, splitCount)
                .flatMap(i -> fetchTimelineChunk(serverId, characterId, startDates.get(i), endDates.get(i), lastCheckedDate))
                .reduce(Long::sum) // Long 타입으로 합산
                .map(Long::intValue); // 최종 결과를 Integer로 변환
    }

    /**
     * 특정 기간의 타임라인 데이터를 조회
     *
     * @param serverId        서버 ID
     * @param characterId     캐릭터 ID
     * @param startDate       시작 날짜
     * @param endDate         종료 날짜
     * @param lastCheckedDate 가장 최근 저장된 date
     * @return Mono<Integer> 태초 아이템 카운트
     */
    private Mono<Long> fetchTimelineChunk(String serverId, String characterId, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime lastCheckedDate) {
        String url = "https://api.neople.co.kr/df/servers/" + serverId
                + "/characters/" + characterId + "/timeline?&code=504,505,506,508,513,520,521"
                + "&startDate=" + startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                + "&endDate=" + endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                + "&limit=100&apikey=" + apiKey;
        System.out.println("Request URL: " + url);

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        Map<String, Object> responseData = objectMapper.readValue(response, new TypeReference<>() {});
                        Map<String, Object> timeline = (Map<String, Object>) responseData.get("timeline");
                        if (timeline == null || timeline.get("rows") == null) {
                            return Mono.just(0L);
                        }
                        List<Map<String, Object>> rows = (List<Map<String, Object>>) timeline.get("rows");

                        System.out.println("Timeline API Response: " + responseData);

                        return Flux.fromIterable(rows)
                                .filter(row -> isAfterLastCheckedDate(row, lastCheckedDate))
                                .filter(row -> {
                                    Map<String, Object> data = (Map<String, Object>) row.get("data");
                                    return isTaechoItem((String) data.get("itemName"));
                                })
                                .count();
                    } catch (Exception e) {
                        System.err.println("타임라인 응답 처리 중 오류: " + e.getMessage());
                        return Mono.just(-1L);
                    }
                })
                .onErrorResume(e -> Mono.just(-2L));
    }

    /**
     * 최근 조회된 날짜 이후인지 확인
     */
    private boolean isAfterLastCheckedDate(Map<String, Object> row, LocalDateTime lastCheckedDate) {
        String dateStr = (String) row.get("date");
        LocalDateTime eventDate = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return eventDate.isAfter(lastCheckedDate);
    }
}
