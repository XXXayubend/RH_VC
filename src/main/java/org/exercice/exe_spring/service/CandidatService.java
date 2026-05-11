package org.exercice.exe_spring.service;

import org.exercice.exe_spring.dto.CandidatDto;
import org.exercice.exe_spring.entity.Candidat;

import java.util.List;

public interface CandidatService {
    CandidatDto createCandidat(CandidatDto candidatDto);
    List<CandidatDto> getAllCAndidats();

}

