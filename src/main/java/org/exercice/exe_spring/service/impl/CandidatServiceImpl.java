package org.exercice.exe_spring.service.impl;

import lombok.AllArgsConstructor;
import org.exercice.exe_spring.dto.CandidatDto;
import org.exercice.exe_spring.entity.Candidat;
import org.exercice.exe_spring.mapper.CandidatMapper;
import org.exercice.exe_spring.repository.CandidatRepository;
import org.exercice.exe_spring.service.CandidatService;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class CnadidatServiceImpl implements CandidatService {

    private CandidatRepository candidatRepository;


    @Override
    public CandidatDto createCandidat(CandidatDto candidatDto) {


        Candidat candidat = CandidatMapper.toDto(candidatDto);
        Candidat saveCandidat = candidatRepository.save(candidat);
        return CandidatMapper.toDto(saveCandidat);
    }
}
