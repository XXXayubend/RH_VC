package org.exercice.exe_spring.repository;

import org.exercice.exe_spring.entity.CVAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CVAnalysisRepository extends JpaRepository<CVAnalysis, Long> {
    Optional<CVAnalysis> findByCandidatId(Long candidatId);
}