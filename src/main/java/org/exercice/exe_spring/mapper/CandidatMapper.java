package org.exercice.exe_spring.mapper;

import org.exercice.exe_spring.dto.CandidatDto;
import org.exercice.exe_spring.entity.Candidat;

public class CandidatMapper {

    // Conversion Entity to Dto
    public static CandidatDto mapToCandidatDto(Candidat candidat) {
        if (candidat == null) {
            return null;
        }

        CandidatDto dto = new CandidatDto();
        dto.setId(candidat.getId());
        dto.setNom(candidat.getNom());
        dto.setEmail(candidat.getEmail());
        dto.setCompetences(candidat.getCompetences());
        dto.setAnneesExperience(candidat.getExperience());
        dto.setCvFileName(candidat.getCvFileName());
        dto.setCvPath(candidat.getCvPath());
        dto.setCvMimeType(candidat.getCvMimeType());
        dto.setCreatedAt(candidat.getCreatedAt());

        return dto;
    }

    public static Candidat mapToCandidat(CandidatDto candidatDto) {
        if (candidatDto == null) {
            return null;
        }

        Candidat candidat = new Candidat();
        candidat.setId(candidatDto.getId());
        candidat.setNom(candidatDto.getNom());
        candidat.setEmail(candidatDto.getEmail());
        candidat.setCompetences(candidatDto.getCompetences());
        candidat.setExperience(candidatDto.getAnneesExperience());
        candidat.setCvFileName(candidatDto.getCvFileName());
        candidat.setCvPath(candidatDto.getCvPath());
        candidat.setCvMimeType(candidatDto.getCvMimeType());
        candidat.setCreatedAt(candidatDto.getCreatedAt());

        return candidat;
    }
}