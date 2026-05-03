package com.internship.tool.service;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.Map;

@Component
public class AiServiceClient {

    private final RestTemplate restTemplate;
    private final String BASE_URL = "http://localhost:5000";

    public AiServiceClient() {
        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(10000);
        factory.setReadTimeout(10000);

        this.restTemplate = new RestTemplate(factory);
    }

    public String describe(String input) {
        return callEndpoint("/describe", input);
    }

    public String recommend(String input) {
        return callEndpoint("/recommend", input);
    }

    public String generateReport(String input) {
        return callEndpoint("/generate-report", input);
    }

    private String callEndpoint(String endpoint, String input) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> request = Map.of("input", input);

            HttpEntity<Map<String, String>> entity =
                    new HttpEntity<>(request, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(
                            BASE_URL + endpoint,
                            entity,
                            String.class
                    );

            return response.getBody();

        } catch (Exception e) {
            return null;
        }
    }
}