package org.exercice.exe_spring.service;

import org.exercice.exe_spring.dto.OpenAIDto;

public interface OpenAIService {
    OpenAIDto evaluateMatch(Long candidatId, Long offreId);
    String compareCandidats(Long offreId, java.util.List<Long> candidatIds);
    String suggestImprovements(Long candidatId, Long offreId);
}
