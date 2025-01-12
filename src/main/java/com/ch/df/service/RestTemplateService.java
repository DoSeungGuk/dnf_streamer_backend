package com.ch.df.service;

import com.ch.df.dto.CharacterSearchResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class RestTemplateService {
        public static void main(String[] args) {
            SpringApplication.run(RestTemplateService.class, args);
        }

        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }

    public ResponseEntity<CharacterSearchResponse> getForEntity(String url, Class<CharacterSearchResponse> characterSearchResponseClass) {
        return null;
    }
}


