package com.tuanyi.voting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class AppConfig {
    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder().build();
    }
}
