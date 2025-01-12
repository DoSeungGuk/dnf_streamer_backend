package com.ch.df.controller;

import com.ch.df.service.DnfService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DnfController {
    private final DnfService dnfService;
    private final WebClient webClient; // WebClient 필드 선언

    public DnfController(DnfService dnfService, WebClient webClient) {
        this.dnfService = dnfService;
        this.webClient = webClient;
    }

    @Value("${api.key:NOT_SET}")
    private String apiKey;

    // 서버 이름과 ID 매핑 테이블
    private static final Map<String, String> SERVER_ID_MAP = Map.of(
            "안톤", "anton",
            "바칼", "bakal",
            "카인", "cain",
            "카시야스", "casillas",
            "디레지에", "diregie",
            "힐더", "hilder",
            "프레이", "prey",
            "시로코", "siroco"
    );

    @GetMapping("/servers")
    //public String getServers() {
        // 기존: RestTemplate restTemplate = new RestTemplate();
        // String url = "https://api.neople.co.kr/df/servers?apikey=" + apiKey;
        // return restTemplate.getForObject(url, String.class);

        // 변경: WebClient 주입(예: 생성자 주입) 후 사용
        // Mono를 리턴하고 싶다면 메소드 시그니처를 Mono<String>으로 변경하고 그대로 반환할 수도 있습니다.
        // 기존처럼 동기로 처리하고 싶다면 block()으로 결과를 받아서 리턴합니다.
    public Mono<String> getServers() {
        String url = "https://api.neople.co.kr/df/servers?apikey=" + apiKey;

        return webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class);

    }

    @GetMapping("/characterDetails")
    public Flux<Map<String, Object>> getCharacterDetailsAsync(
            @RequestParam String server,
            @RequestParam String characterName
    ) {
        String serverId = getServerId(server);

        // 캐릭터 검색 URL
        String searchUrl = "https://api.neople.co.kr/df/servers/"
                + serverId
                + "/characters?characterName=" + characterName
                + "&apikey=" + apiKey;

        // (1) 캐릭터 목록 조회
        return webClient.get()
                .uri(searchUrl)
                .retrieve()
                .bodyToMono(String.class) // Mono<String> 응답
                .flatMapMany(response -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        // JSON 파싱
                        Map<String, Object> searchResult = objectMapper.readValue(
                                response,
                                new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {}
                        );

                        List<Map<String, Object>> rows = (List<Map<String, Object>>) searchResult.get("rows");

                        // (2) 각 캐릭터 상세 정보를 비동기로 조회
                        return Flux.fromIterable(rows)
                                .flatMap(row -> {
                                    String characterServerId = (String) row.get("serverId");
                                    String characterId = (String) row.get("characterId");

                                    String detailUrl = "https://api.neople.co.kr/df/servers/"
                                            + characterServerId
                                            + "/characters/" + characterId
                                            + "?apikey=" + apiKey;

                                    // WebClient 비동기 호출
                                    return webClient.get()
                                            .uri(detailUrl)
                                            .retrieve()
                                            .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                                            .onErrorResume(e -> {
                                                System.err.println("캐릭터 상세 정보 호출 오류: " + e.getMessage());
                                                return Mono.empty(); // 오류 시 빈 데이터 반환
                                            });
                                })
                                .collectList() // 비동기 데이터 저장 전에 Flux를 List로 변환
                                .flatMapMany(characterDetails -> {
                                    // 저장 로직 추가
                                    dnfService.saveMatchingCharacters(characterDetails);
                                    return Flux.fromIterable(characterDetails); // 저장 후 원본 데이터를 반환
                                });
                    } catch (Exception e) {
                        return Flux.error(e); // JSON 파싱 오류 시 Flux 에러 반환
                    }
                });
    }


    // 서버 ID 변환 메서드
    private String getServerId(String serverName) {
        // 서버 이름이 이미 영어 서버 ID인 경우 그대로 반환
        if (SERVER_ID_MAP.containsValue(serverName)) {
            return serverName;
        }
        // 서버 이름이 한글인 경우 매핑 테이블에서 변환
        return SERVER_ID_MAP.getOrDefault(serverName, "all");
    }
    @GetMapping("/saveMatchingCharacters")
    public Mono<String> saveMatchingCharactersAsync(
            @RequestParam String server,
            @RequestParam String characterName
    ) {
        // 캐릭터 세부 정보를 비동기로 가져오기
        return getCharacterDetailsAsync(server, characterName)
                .collectList() // Flux를 List로 변환
                .flatMap(characterDetails -> {
                    // 비동기로 서비스 호출하여 저장
                    return Mono.fromRunnable(() -> dnfService.saveMatchingCharacters((List<Map<String, Object>>) characterDetails))
                            .then(Mono.just("Matching characters saved successfully."));
                })
                .onErrorResume(e -> {
                    System.err.println("캐릭터 저장 중 오류 발생: " + e.getClass());
                    return Mono.just("Failed to save matching characters.");
                });
    }}

