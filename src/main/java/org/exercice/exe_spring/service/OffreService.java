package org.exercice.exe_spring.service;

import org.exercice.exe_spring.dto.OffreDto;
import org.exercice.exe_spring.entity.Offre;

import java.util.List;

public interface OffreService {

    OffreDto createOffre(OffreDto offreDto);
    List<OffreDto> getAllOffres();
}
