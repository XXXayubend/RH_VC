package org.exercice.exe_spring.controller;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exercice.exe_spring.dto.CandidatDto;
import org.exercice.exe_spring.entity.Candidat;
import org.exercice.exe_spring.service.CandidatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/api/candidats")
public class CandidatController {

    @Autowired
    private CandidatService candidatService;

    //créer add candidat REST API
    @PostMapping
    public ResponseEntity<CandidatDto> createCandidat(@Valid @RequestBody CandidatDto candidatDto){
        log.info("Requette POST /api/candidats - body: {}", candidatDto);
        CandidatDto savedCandidat = candidatService.createCandidat(candidatDto);
        log.info("Candidat crée avec success,id {}", savedCandidat.getId());
        return new ResponseEntity<>(savedCandidat, HttpStatus.CREATED);
    }

    // Lister tous les candidats
    @GetMapping
    public List<CandidatDto> getAllCandidats() {
        log.debug("Requete GET /api/candidats");
        return candidatService.getAllCAndidats();
    }
}
