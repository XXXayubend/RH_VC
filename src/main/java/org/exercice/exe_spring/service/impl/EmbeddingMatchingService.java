package org.exercice.exe_spring.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exercice.exe_spring.client.EmbeddingClient;
import org.exercice.exe_spring.dto.CandidatDto;
import org.exercice.exe_spring.dto.OffreDto;
import org.exercice.exe_spring.dto.AIDto;
import org.exercice.exe_spring.entity.CVAnalysis;
import org.exercice.exe_spring.repository.CVAnalysisRepository;
import org.exercice.exe_spring.service.CandidatService;
import org.exercice.exe_spring.service.OffreService;
import org.exercice.exe_spring.service.AIService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class EmbeddingMatchingService implements AIService {

    private final CandidatService candidatService;
    private final OffreService offreService;
    private final CVAnalysisRepository cvAnalysisRepository;
    private final EmbeddingClient embeddingClient;

    @Override
    public AIDto evaluateMatch(Long candidatId, Long offreId) {
        // Récupération des données
        String cvText = cvAnalysisRepository.findByCandidatId(candidatId)
                .map(CVAnalysis::getExtractedText)
                .orElseThrow(() -> new RuntimeException("CV non analysé pour le candidat " + candidatId));
        CandidatDto candidat = candidatService.getCandidatById(candidatId);
        OffreDto offre = offreService.getOffreById(offreId);

        double similarity = embeddingClient.computeSimilarity(cvText, offre.getCompetencesRequises());
        double score = similarity * 100;

        // Construction du DTO
        AIDto dto = new AIDto();
        dto.setScore(score);
        return dto;
    }
}