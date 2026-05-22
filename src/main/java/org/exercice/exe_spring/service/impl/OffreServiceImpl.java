package org.exercice.exe_spring.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exercice.exe_spring.dto.OffreDto;
import org.exercice.exe_spring.entity.Offre;
import org.exercice.exe_spring.exception.DuplicateTitreException;
import org.exercice.exe_spring.exception.ResourceNotFoundException;
import org.exercice.exe_spring.mapper.OffreMapper;
import org.exercice.exe_spring.repository.OffreRepository;
import org.exercice.exe_spring.service.OffreService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class OffreServiceImpl implements OffreService {

    private final OffreRepository offreRepository;

    @Override
    public OffreDto createOffre(OffreDto offreDto) {
        log.debug("Tentative de création offre : {}", offreDto.getTitre());

        if (offreRepository.existsByTitre(offreDto.getTitre())) {
            log.error("Titre deja utilise: {}", offreDto.getTitre());
            throw new DuplicateTitreException("Une offre avec ce titre existe deja : " + offreDto.getTitre());
        }

        Offre offre = OffreMapper.mapToOffre(offreDto);
        Offre savedOffre = offreRepository.save(offre);
        log.info("Offre sauvegarder avec id {}", savedOffre.getId());
        return OffreMapper.mapToOffreDto(savedOffre);
    }

    @Override
    public List<OffreDto> getAllOffres() {
        return offreRepository.findAll()
                .stream()
                .map(OffreMapper::mapToOffreDto)
                .collect(Collectors.toList());
    }

    @Override
    public OffreDto getOffreById(Long id) {
        Offre offre = offreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offre non trouvée avec l'id: " + id));
        return OffreMapper.mapToOffreDto(offre);
    }
}