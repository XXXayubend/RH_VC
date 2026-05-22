package org.exercice.exe_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidatWithCVDto {
    private String nom;
    private String email;
    private String competances;
    private Integer anneesExperience;
    private MultipartFile cv;
}