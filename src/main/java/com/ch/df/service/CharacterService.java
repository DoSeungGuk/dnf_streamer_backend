package com.ch.df.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class CharacterService {

    @Value("${api.key:NOT_SET}") // application.properties 또는 환경변수에서 API 키를 가져옴
    private String apiKey;

    private final RestTemplate restTemplate;

    public CharacterService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> fetchCharacterFromAllServers(String characterName) {
        try {
            // URL 인코딩 처리
            String encodedCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8.toString());

            // API 호출 URL 생성
            String url = UriComponentsBuilder.fromHttpUrl("https://api.neople.co.kr/df/servers/all/characters")
                    .queryParam("characterName", encodedCharacterName)
                    .queryParam("apikey", apiKey)
                    .toUriString();

            // API 호출
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch character from all servers.", e);
        }
    }
}
