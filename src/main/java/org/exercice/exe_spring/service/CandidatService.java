package org.exercice.exe_spring.service;

import org.exercice.exe_spring.dto.CandidatDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CandidatService {
    CandidatDto createCandidat(CandidatDto candidatDto);
    List<CandidatDto> getAllCandidats();
    CandidatDto createCandidatWithCV(CandidatDto candidatDto, MultipartFile file) throws Exception;
    CandidatDto getCandidatById(Long id);
    byte[] downloadCV(Long id) throws IOException;
    // CandidatDto updateCandidatWithCV(Long id, CandidatDto candidatDto, MultipartFile file) throws IOException;
    // void deleteCandidat(Long id);

}

