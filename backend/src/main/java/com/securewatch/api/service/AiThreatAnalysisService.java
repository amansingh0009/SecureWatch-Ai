package com.securewatch.api.service;

import com.securewatch.api.entity.Vulnerability;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AiThreatAnalysisService {
    private final RestClient restClient = RestClient.create();

    @Value("${app.ai.api-key}")
    private String apiKey;

    @Value("${app.ai.endpoint}")
    private String endpoint;

    @Value("${app.ai.model}")
    private String model;

    public String analyze(String target, List<Vulnerability> vulnerabilities, int score) {
        if (apiKey == null || apiKey.isBlank()) {
            return fallback(target, vulnerabilities, score);
        }
        String prompt = "Act as a cybersecurity analyst. Explain risk and remediation for target " + target
                + ". Risk score: " + score + ". Findings: " + vulnerabilities.stream()
                .map(v -> v.getSeverity() + " " + v.getName() + ": " + v.getDescription())
                .toList();
        try {
            Map<String, Object> body = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role", "system", "content", "You write concise, practical vulnerability reports."),
                            Map.of("role", "user", "content", prompt)
                    ),
                    "temperature", 0.2
            );
            Map<?, ?> response = restClient.post()
                    .uri(endpoint)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);
            List<?> choices = (List<?>) response.get("choices");
            Map<?, ?> choice = (Map<?, ?>) choices.get(0);
            Map<?, ?> message = (Map<?, ?>) choice.get("message");
            return String.valueOf(message.get("content"));
        } catch (RuntimeException ex) {
            return fallback(target, vulnerabilities, score);
        }
    }

    private String fallback(String target, List<Vulnerability> vulnerabilities, int score) {
        if (vulnerabilities.isEmpty()) {
            return "No critical issues were detected for " + target + ". Continue monitoring headers, TLS, exposed ports, and dependency hygiene.";
        }
        return "SecureWatch AI found " + vulnerabilities.size() + " issue(s) on " + target
                + " with a risk score of " + score
                + ". Prioritize critical and high findings, add missing browser protections, harden TLS, and validate all input on the server.";
    }
}
