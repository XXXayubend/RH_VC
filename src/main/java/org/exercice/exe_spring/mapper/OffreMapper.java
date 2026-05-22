package org.exercice.exe_spring.mapper;

import org.exercice.exe_spring.dto.OffreDto;
import org.exercice.exe_spring.entity.Offre;

public class OffreMapper {

    // Entity to Dto
    public static OffreDto mapToOffreDto(Offre offre) {
        if (offre == null) {
            return null;
        }
        OffreDto dto = new OffreDto();
        dto.setId(offre.getId());
        dto.setTitre(offre.getTitre());
        dto.setCompetencesRequises(offre.getCompetence());
        return dto;
    }

    // Dto to Entity
    public static Offre mapToOffre(OffreDto offreDto) {
        if (offreDto == null) {
            return null;
        }
        Offre offre = new Offre();
        offre.setId(offreDto.getId());
        offre.setTitre(offreDto.getTitre());
        offre.setCompetence(offreDto.getCompetencesRequises());
        return offre;
    }
}