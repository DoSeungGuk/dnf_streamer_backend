package com.ch.df.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import javax.sql.DataSource;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        // baseUrl, defaultHeader, timeout 등 필요한 설정을 여기에 적용해도 됩니다.
        return builder
                //.baseUrl("https://api.neople.co.kr/df")  // base URL을 지정할 수도 있음
                .build();
    }
}
