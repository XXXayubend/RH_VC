package org.exercice.exe_spring.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exercice.exe_spring.dto.AIDto;
import org.exercice.exe_spring.service.AIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIService aiService;

    @GetMapping("/evaluate/{candidatId}/{offreId}")
    public ResponseEntity<AIDto> evaluateMatch(@PathVariable Long candidatId, @PathVariable Long offreId) {
        log.info("Evaluating match: candidat={}, offre={}", candidatId, offreId);
        return ResponseEntity.ok(aiService.evaluateMatch(candidatId, offreId));
    }
}