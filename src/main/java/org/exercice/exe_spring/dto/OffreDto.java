package org.exercice.exe_spring.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OffreDto {
    private Long id;

    @NotNull(message = "Le titre est obligatoire")
    @NotBlank(message = "Le titre ne peut pas etre vide")
    private String titre;

    @NotNull(message = "Les compétances sont obligatoires")
    @NotBlank(message = "Les compétances ne peut pas etre vide")
    private String competencesRequises;
}
