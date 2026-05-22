package org.exercice.exe_spring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exercice.exe_spring.entity.CVAnalysis;
import org.exercice.exe_spring.entity.Candidat;
import org.exercice.exe_spring.repository.CVAnalysisRepository;
import org.exercice.exe_spring.repository.CandidatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService {

    private final TextExtractService textExtractorService;
    private final CVAnalysisRepository cvAnalysisRepository;
    private final CandidatRepository candidatRepository;

    // ✅ NOUVEAU : Méthode pour analyser et lier à un candidat
    @Transactional
    public String analyzeAndSaveForCandidat(Long candidatId, MultipartFile cvFile) throws Exception {
        Candidat candidat = candidatRepository.findById(candidatId)
                .orElseThrow(() -> new RuntimeException("Candidat non trouvé"));

        String extractedText = textExtractorService.extractTextFromCV(cvFile);

        CVAnalysis analysis = cvAnalysisRepository.findByCandidatId(candidatId)
                .orElse(new CVAnalysis());
        analysis.setCandidat(candidat);
        analysis.setExtractedText(extractedText);
        analysis.setTextLength(extractedText.length());

        cvAnalysisRepository.save(analysis);

        log.info("Analyse CV sauvegardée pour candidat {}", candidatId);
        return extractedText;
    }

}