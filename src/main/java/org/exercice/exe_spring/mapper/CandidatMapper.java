package org.exercice.exe_spring.mapper;

import org.exercice.exe_spring.dto.CandidatDto;
import org.exercice.exe_spring.entity.Candidat;

public class CandidatMapper {


    // Conversion Entity to Dto
    public static CandidatDto mapToCandidatDto(Candidat candidat){
        return new CandidatDto(
                candidat.getId(),
                candidat.getNom(),
                candidat.getEmail(),
                candidat.getCompetence(),
                candidat.getExperience()
        );
    }

    // Conversion Dto to Entity
    public static Candidat mapToCandidat(CandidatDto candidatDto){
        return new Candidat(
                candidatDto.getId(),
                candidatDto.getNom(),
                candidatDto.getEmail(),
                candidatDto.getCompetances(),
                candidatDto.getAnneesExperience()
        );
    }

}
