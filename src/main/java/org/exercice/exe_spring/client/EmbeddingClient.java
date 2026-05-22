package org.exercice.exe_spring.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class EmbeddingClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public EmbeddingClient(@Value("${embedding.service.url:http://localhost:8000}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    public double computeSimilarity(String cvText, String jobText) {
        MatchRequest request = new MatchRequest(cvText, jobText);
        MatchResponse response = restTemplate.postForObject(baseUrl + "/match", request, MatchResponse.class);
        if (response == null) {
            throw new RuntimeException("Réponse nulle du micro-service d'embedding");
        }
        return response.score();
    }

    private record MatchRequest(String cv_text, String job_text) {}
    private record MatchResponse(double score, String recommendation) {}
}