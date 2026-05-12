package org.exercice.exe_spring.repository;

import jakarta.validation.constraints.NotBlank;
import org.exercice.exe_spring.entity.Offre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OffreRepository extends JpaRepository<Offre, Long> {
    boolean existsByTitre(String titre);
}
