package com.trinetra.verdict.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroqService {

    @Value("${groq.api.key:}")
    private String apiKey;

    @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String apiUrl;

    @Value("${groq.model:llama-3.3-70b-versatile}")
    private String modelName;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String SYSTEM_PROMPT = """
        You are TriNetra AI's real-time doorstep multimodal voice intelligence parser for reverse logistics and return fraud prevention in India.
        Analyze the courier's voice transcript, which may be in English, Hindi, Tamil, Hinglish, or code-switched phrases.
        
        Extract the structured condition assessment. You must reply ONLY with a valid JSON object conforming strictly to this schema:
        {
          "anomaly_detected": boolean,
          "seal_integrity_status": "INTACT" | "TAMPER_SUSPECTED" | "BROKEN" | "RE_TAPED",
          "weight_assessment": "NORMAL" | "WEIGHT_LIGHT_SUSPECTED" | "WEIGHT_HEAVY_SUSPECTED" | "EMPTY_PACKAGE",
          "suspected_issue": "NONE" | "COUNTERFEIT_SWAP" | "TRANSIT_DAMAGE" | "MISSING_ITEM" | "SEAL_TAMPERING",
          "detected_signals": ["array of exact keywords or indicator phrases found"],
          "confidence_score": number between 0.0 and 1.0,
          "reasoning_summary": "Concise 1-2 sentence explanation of the assessment"
        }
        """;

    public String analyzeTranscript(String transcript) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Groq API Key is not configured in backend properties.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey.trim());

        Map<String, Object> systemMessage = Map.of(
                "role", "system",
                "content", SYSTEM_PROMPT
        );

        Map<String, Object> userMessage = Map.of(
                "role", "user",
                "content", "Courier Voice Transcript: \"" + transcript + "\""
        );

        Map<String, Object> responseFormat = Map.of("type", "json_object");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", modelName);
        requestBody.put("messages", List.of(systemMessage, userMessage));
        requestBody.put("temperature", 0.1);
        requestBody.put("response_format", responseFormat);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List choices = (List) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map firstChoice = (Map) choices.get(0);
                    Map message = (Map) firstChoice.get("message");
                    if (message != null) {
                        return (String) message.get("content");
                    }
                }
            }
            throw new RuntimeException("Unexpected response from Groq API: " + response.getStatusCode());
        } catch (Exception e) {
            throw new RuntimeException("Groq LLM Request Failed: " + e.getMessage(), e);
        }
    }
}
