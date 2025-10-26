package com.example.autoresponse.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateResponse(String complaintText) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + geminiApiKey;

        // Request body
        Map<String, Object> content = Map.of(
                "parts", List.of(Map.of("text", "Citizen complaint: " + complaintText))
        );

        Map<String, Object> request = Map.of(
                "contents", List.of(content)
        );

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        // Call Gemini API
        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);

        if (response == null || !response.containsKey("candidates")) {
            return "No response generated from Gemini.";
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");

        if (candidates.isEmpty()) {
            return "No response candidates received.";
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> firstCandidate = candidates.get(0);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> contentList = (List<Map<String, Object>>) firstCandidate.get("content");

        if (contentList == null || contentList.isEmpty()) {
            return "No content generated.";
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> parts = (List<Map<String, Object>>) contentList.get(0).get("parts");

        if (parts == null || parts.isEmpty()) {
            return "No parts generated.";
        }

        return parts.get(0).get("text").toString();
    }
}