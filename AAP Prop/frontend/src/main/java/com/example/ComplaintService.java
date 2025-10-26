package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

    // Inject Gemini API key from application.properties
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    // ObjectMapper for JSON parsing
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    public Complaint saveComplaint(Complaint complaint) {
        complaint.setStatus("Pending"); // default status
        return complaintRepository.save(complaint);
    }

    public List<Complaint> getComplaintsByUsername(String username) {
        return complaintRepository.findByUsername(username);
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public Complaint updateComplaintStatus(Long id, String status) {
        Complaint complaint = complaintRepository.findById(id).orElseThrow();
        complaint.setStatus(status);
        return complaintRepository.save(complaint);
    }

    // ✅ NEW: update autoComment
    public Complaint updateComplaintAutoComment(Long id, String autoComment) {
        Complaint complaint = complaintRepository.findById(id).orElseThrow();
        complaint.setAutoComment(autoComment);
        return complaintRepository.save(complaint);
    }

    /**
     * Generates a short AI acknowledgement/comment for a complaint using Gemini (Generative Language API).
     * Returns a short string fallback if anything fails.
     */
    public String generateAutoComment(String complaintDescription) {
        if (complaintDescription == null || complaintDescription.isBlank()) {
            return "Thank you for your report. We will investigate this issue.";
        }

        try {
            // Build request body according to the Generative Language API for generateContent
            Map<String, Object> body = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", "Citizen complaint: " + complaintDescription +
                                            "\n\nWrite a SHORT 1-2 sentence official acknowledgement.")
                            ))
                    )
            );

            // Build URL with API key as query param
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + geminiApiKey;

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            // Call the API
            String response = restTemplate.postForObject(url, entity, String.class);

            if (response == null || response.isBlank()) {
                return "Thank you for your report. We will investigate this issue.";
            }

            // Parse the response JSON to extract the candidate text
            JsonNode root = objectMapper.readTree(response);
            JsonNode candidateTextNode = root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            String autoComment = candidateTextNode.asText("").trim();

            // Keep it short — if Gemini returned long text, truncate to ~200 chars
            if (autoComment.length() > 400) {
                autoComment = autoComment.substring(0, 400).trim() + "...";
            }

            if (autoComment.isEmpty()) {
                return "Thank you for your report. We will investigate this issue.";
            }

            return autoComment;

        } catch (Exception e) {
            // Log the exception in console (or preferably use a logger)
            e.printStackTrace();
            // Fallback message
            return "Thank you for reporting this issue. We will review and update you shortly.";
        }
    }
}