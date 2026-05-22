package org.exercice.exe_spring.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exercice.exe_spring.client.EmbeddingClient;
import org.exercice.exe_spring.dto.CandidatDto;
import org.exercice.exe_spring.dto.OffreDto;
import org.exercice.exe_spring.dto.OpenAIDto;
import org.exercice.exe_spring.entity.CVAnalysis;
import org.exercice.exe_spring.repository.CVAnalysisRepository;
import org.exercice.exe_spring.service.CandidatService;
import org.exercice.exe_spring.service.OffreService;
import org.exercice.exe_spring.service.OpenAIService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class EmbeddingMatchingService implements OpenAIService {

    private final CandidatService candidatService;
    private final OffreService offreService;
    private final CVAnalysisRepository cvAnalysisRepository;
    private final EmbeddingClient embeddingClient;

    @Override
    public OpenAIDto evaluateMatch(Long candidatId, Long offreId) {
        // Récupération des données
        String cvText = cvAnalysisRepository.findByCandidatId(candidatId)
                .map(CVAnalysis::getExtractedText)
                .orElseThrow(() -> new RuntimeException("CV non analysé pour le candidat " + candidatId));
        CandidatDto candidat = candidatService.getCandidatById(candidatId);
        OffreDto offre = offreService.getOffreById(offreId);

        // Calcul de la similarité sémantique
        double similarity = embeddingClient.computeSimilarity(cvText, offre.getCompetencesRequises());

        // Application du bonus d'expérience
        int experience = candidat.getAnneesExperience() != null ? candidat.getAnneesExperience() : 0;
        double experienceBonus = Math.min(0.20, experience * 0.05);
        double adjustedSimilarity = Math.min(1.0, similarity + experienceBonus);
        double score = adjustedSimilarity * 100;

        // Recommandation
        String recommandation;
        if (adjustedSimilarity >= 0.75) recommandation = "RECOMMANDE";
        else if (adjustedSimilarity >= 0.55) recommandation = "PEUT_ETRE";
        else recommandation = "A_REVOIR";

        // Construction du DTO
        OpenAIDto dto = new OpenAIDto();
        dto.setScore(score);
        dto.setRecommandation(recommandation);
        dto.setAnalyse(String.format("Similarité = %.2f (bonus expérience : %.0f%%)", similarity, experienceBonus * 100));
        dto.setPointsForts(String.format("%d années d'expérience, analyse sémantique via Jina", experience));
        dto.setPointsFaibles("Évaluation automatique, sans analyse fine");
        dto.setResume(recommandation.equals("RECOMMANDE") ? "Candidat recommandé" : "Candidat à améliorer");
        return dto;
    }

    @Override
    public String compareCandidats(Long offreId, List<Long> candidatIds) {
        // Implémentation optionnelle : trier les candidats par score
        return "{\"message\":\"Comparaison non disponible avec ce service\"}";
    }

    @Override
    public String suggestImprovements(Long candidatId, Long offreId) {
        return "{\"message\":\"Suggestions non disponibles\"}";
    }
}