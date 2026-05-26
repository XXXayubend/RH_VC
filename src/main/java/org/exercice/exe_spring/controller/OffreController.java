package org.exercice.exe_spring.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exercice.exe_spring.dto.OffreDto;
import org.exercice.exe_spring.service.OffreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/offres")
public class OffreController {

    private final OffreService offreService;

    @PostMapping
    public ResponseEntity<OffreDto> createOffre(@Valid @RequestBody OffreDto offreDto) {
        log.info("POST /api/offres - body: {}", offreDto);
        OffreDto saved = offreService.createOffre(offreDto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping
    public List<OffreDto> getAllOffres() {
        return offreService.getAllOffres();
    }
}