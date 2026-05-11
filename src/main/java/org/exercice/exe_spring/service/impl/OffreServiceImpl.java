package org.exercice.exe_spring.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exercice.exe_spring.dto.OffreDto;
import org.exercice.exe_spring.entity.Offre;
import org.exercice.exe_spring.mapper.OffreMapper;
import org.exercice.exe_spring.repository.OffreRepository;
import org.exercice.exe_spring.service.OffreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class OffreServiceImpl implements OffreService {

    @Autowired
    private final OffreRepository offreRepository;

    @Override
    public OffreDto createOffre(OffreDto offreDto) {
        log.debug("Tentative de création offre : {}", offreDto.getTitre());
        Offre offre = OffreMapper.mapToOffre(offreDto);
        Offre savedOffre = offreRepository.save(offre);
        log.info("Offre sauvegarder evec id {}", savedOffre.getId());
        return OffreMapper.mapToOffreDto(savedOffre);
    }

    @Override
    public List<OffreDto> getAllOffres() {
        return offreRepository.findAll()
                .stream()
                .map(OffreMapper::mapToOffreDto)
                .collect(Collectors.toList());
    }
}
