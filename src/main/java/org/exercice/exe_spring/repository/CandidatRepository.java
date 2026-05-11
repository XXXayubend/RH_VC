package org.exercice.exe_spring.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.exercice.exe_spring.entity.Candidat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidatRepository  extends JpaRepository<Candidat, Long> {
    Optional<Candidat> findByEmail(@Email(message = "Email invalide") @NotBlank(message = "L'email est obligatoire") String email);
}
