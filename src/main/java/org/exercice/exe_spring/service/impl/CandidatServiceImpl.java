package org.exercice.exe_spring.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exercice.exe_spring.dto.CandidatDto;
import org.exercice.exe_spring.entity.Candidat;
import org.exercice.exe_spring.exception.DuplicateEmailException;
import org.exercice.exe_spring.mapper.CandidatMapper;
import org.exercice.exe_spring.repository.CandidatRepository;
import org.exercice.exe_spring.service.CandidatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.DuplicateFormatFlagsException;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@AllArgsConstructor
public class CandidatServiceImpl implements CandidatService {

    @Autowired
    private final CandidatRepository candidatRepository;


    @Override
    public CandidatDto createCandidat(CandidatDto candidatDto) {

        log.debug("Tentative de création candidat: {}", candidatDto.getEmail());

        if (candidatRepository.findByEmail(candidatDto.getEmail()).isPresent()) {
            log.error("Email féja utilisée: {}", candidatDto.getEmail());
            throw new DuplicateEmailException("Un candidat avec cet email existe deja : " + candidatDto.getEmail());
        }

        Candidat candidat = CandidatMapper.mapToCandidat(candidatDto);
        Candidat savedCandidat = candidatRepository.save(candidat);
        log.info("Candidat sauvegarder evec id {}", savedCandidat.getId());
        return CandidatMapper.mapToCandidatDto(savedCandidat);
    }

    @Override
    public List<CandidatDto> getAllCAndidats() {
        log.debug("Récupération de tous les candidats");
        return candidatRepository.findAll().
                stream()
                .map(CandidatMapper::mapToCandidatDto)
                .collect(Collectors.toList());
    }

}
