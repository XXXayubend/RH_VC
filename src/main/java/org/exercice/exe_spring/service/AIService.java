package org.exercice.exe_spring.service;

import org.exercice.exe_spring.dto.AIDto;

public interface AIService {
    AIDto evaluateMatch(Long candidatId, Long offreId);
}
