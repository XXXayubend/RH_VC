package org.exercice.exe_spring.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exercice.exe_spring.dto.OpenAIDto;
import org.exercice.exe_spring.service.OpenAIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/ai")
public class OpenAIController {

    private final OpenAIService openRouterService;

    @PostMapping("/evaluate/{candidatId}/{offreId}")
    public ResponseEntity<OpenAIDto> evaluateMatch(
            @PathVariable Long candidatId,
            @PathVariable Long offreId) {
        log.info("Évaluation OpenRouter: candidat {} / offre {}", candidatId, offreId);
        return ResponseEntity.ok(openRouterService.evaluateMatch(candidatId, offreId));
    }

    @GetMapping("/score/{candidatId}/{offreId}")
    public ResponseEntity<Map<String, Object>> getSimpleScore(
            @PathVariable Long candidatId,
            @PathVariable Long offreId) {
        OpenAIDto response = openRouterService.evaluateMatch(candidatId, offreId);
        return ResponseEntity.ok(Map.of(
                "score", response.getScore(),
                "recommandation", response.getRecommandation(),
                "resume", response.getResume()
        ));
    }

    @PostMapping("/compare/{offreId}")
    public ResponseEntity<String> compareCandidats(
            @PathVariable Long offreId,
            @RequestBody List<Long> candidatIds) {
        return ResponseEntity.ok(openRouterService.compareCandidats(offreId, candidatIds));
    }

    @GetMapping("/suggest/{candidatId}/{offreId}")
    public ResponseEntity<String> suggestImprovements(
            @PathVariable Long candidatId,
            @PathVariable Long offreId) {
        return ResponseEntity.ok(openRouterService.suggestImprovements(candidatId, offreId));
    }
}