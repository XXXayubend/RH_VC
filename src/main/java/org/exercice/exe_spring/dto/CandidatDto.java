package org.exercice.exe_spring.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidatDto {
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @Email(message = "Email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;


    private String competences;

    @PositiveOrZero(message = "L'experience doit étre etre positive ou nulle")
    private Integer anneesExperience;

    private String CvFileName;

    private String cvPath;
    private String cvMimeType;
    private LocalDateTime createdAt;

    public CandidatDto(Long id, String nom, String email, String competences, Integer anneesExperience) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.competences = competences;
        this.anneesExperience = anneesExperience;
    }
}
