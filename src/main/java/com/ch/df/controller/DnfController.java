package com.ch.df.controller;

import com.ch.df.service.DnfService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DnfController {

    private final DnfService dnfService;

    public DnfController(DnfService dnfService) {
        this.dnfService = dnfService;
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
    public String getServers() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.neople.co.kr/df/servers?apikey=" + apiKey;
        return restTemplate.getForObject(url, String.class);
    }

    @GetMapping("/characterDetails")
    public List<Map<String, Object>> getCharacterDetails(
            @RequestParam String server,
            @RequestParam String characterName
    ) {
        RestTemplate restTemplate = new RestTemplate();
        List<Map<String, Object>> characterDetails = new ArrayList<>();

        // 서버 ID 변환
        String serverId = getServerId(server);

        // searchCharacter 호출
        String searchUrl = "https://api.neople.co.kr/df/servers/" + serverId + "/characters?characterName=" + characterName + "&apikey=" + apiKey;

        try {
            // searchCharacter API 호출
            String searchResponse = restTemplate.getForObject(searchUrl, String.class);

            // JSON 파싱 (rows 리스트 추출)
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> searchResult = objectMapper.readValue(searchResponse, Map.class);
            List<Map<String, String>> rows = (List<Map<String, String>>) searchResult.get("rows");

            // rows를 기반으로 각 캐릭터의 상세 정보 조회
            for (Map<String, String> row : rows) {
                String characterServerId = row.get("serverId");
                String characterId = row.get("characterId");

                // 캐릭터 기본 정보 URL
                String detailUrl = "https://api.neople.co.kr/df/servers/" + characterServerId + "/characters/" + characterId
                        + "?apikey=" + apiKey;

                // API 호출
                try {
                    Map<String, Object> characterInfo = restTemplate.getForObject(detailUrl, Map.class);
                    if (characterInfo != null) {
                        characterDetails.add(characterInfo);
                    }
                } catch (Exception e) {
                    System.err.println("캐릭터 상세 정보 API 호출 오류 (Server ID: " + characterServerId + ", Character ID: " + characterId + "): " + e.getMessage());
                }
            }
            dnfService.saveMatchingCharacters(characterDetails);

        } catch (Exception e) {
            System.err.println("searchCharacter API 호출 오류: " + e.getMessage());
        }

        return characterDetails;
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
    public String saveMatchingCharacters(@RequestParam String server, @RequestParam String characterName) {
        // 캐릭터 세부 정보 가져오기
        List<Map<String, Object>> characterDetails = getCharacterDetails(server, characterName);

        // 서비스에 캐릭터 정보 저장 요청
        dnfService.saveMatchingCharacters(characterDetails);

        return "Matching characters saved successfully.";
    }
}
