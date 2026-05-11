package org.exercice.exe_spring.mapper;

import org.exercice.exe_spring.dto.OffreDto;
import org.exercice.exe_spring.entity.Offre;

public class OffreMapper {


    // Entity to Dto
    public static OffreDto mapToOffreDto(Offre offre){
        return new OffreDto(
                offre.getId(),
                offre.getTitre(),
                offre.getCompetence()
        );
    }

    // Dto to Entity
    public static Offre mapToOffre(OffreDto offreDto){
        return new Offre(
                offreDto.getId(),
                offreDto.getTitre(),
                offreDto.getCompetancesRequises()
        );
    }
}
