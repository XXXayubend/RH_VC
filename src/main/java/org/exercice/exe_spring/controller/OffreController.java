package org.exercice.exe_spring.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exercice.exe_spring.dto.OffreDto;
import org.exercice.exe_spring.entity.Offre;
import org.exercice.exe_spring.service.OffreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/api/offres")
public class OffreController {

    private OffreService offreService;

    // créer add offre REST API
    @PostMapping
    public ResponseEntity<OffreDto> createOffre(@Valid @RequestBody OffreDto offreDto) {
        log.info("Requete POST /api/offres - body: {}", offreDto);
        OffreDto savedOffre = offreService.createOffre(offreDto);
        log.info("Offre créer avec success, id {}", savedOffre.getId());
        return new ResponseEntity<>(savedOffre, HttpStatus.CREATED);
    }

    // Lister tous les offres
    @GetMapping
    public List<OffreDto> getAllOffres() {
        log.debug("Requetes GET /api/offres");
        return offreService.getAllOffres();
    }
}
