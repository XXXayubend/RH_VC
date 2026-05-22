package org.exercice.exe_spring.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exercice.exe_spring.dto.CandidatDto;
import org.exercice.exe_spring.service.CandidatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@AllArgsConstructor
//@CrossOrigin
@RestController
@RequestMapping("/api/candidats")
public class CandidatController {

    @Autowired
    private CandidatService candidatService;

    // Créer un candidat SANS CV (existant)
    @PostMapping
    public ResponseEntity<CandidatDto> createCandidat(@Valid @RequestBody CandidatDto candidatDto){
        log.info("Requete POST /api/candidats - body: {}", candidatDto);
        CandidatDto savedCandidat = candidatService.createCandidat(candidatDto);
        log.info("Candidat crée avec succes, id {}", savedCandidat.getId());
        return new ResponseEntity<>(savedCandidat, HttpStatus.CREATED);
    }

    // Créer un candidat AVEC CV (upload de fichier)
    @PostMapping("/with-cv")
    public ResponseEntity<?> createCandidatWithCV(
            @RequestParam("nom") String nom,
            @RequestParam("email") String email,
            @RequestParam("competances") String competances,
            @RequestParam("anneesExperience") Integer anneesExperience,
            @RequestParam("cv") MultipartFile cv) {

        log.info("Requete POST /api/candidats/with-cv - nom: {}, email: {}", nom, email);

        try {
            CandidatDto candidatDto = new CandidatDto();
            candidatDto.setNom(nom);
            candidatDto.setEmail(email);
            candidatDto.setCompetences(competances);
            candidatDto.setAnneesExperience(anneesExperience);

            CandidatDto savedCandidat = candidatService.createCandidatWithCV(candidatDto, cv);
            log.info("Candidat avec CV crée avec succes, id {}", savedCandidat.getId());

            return new ResponseEntity<>(savedCandidat, HttpStatus.CREATED);
        } catch (IOException e) {
            log.error("Erreur lors du téléchargement du CV", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du téléchargement du CV: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("Erreur lors de la création du candidat", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Lister tous les candidats
    @GetMapping
    public List<CandidatDto> getAllCandidats() {
        log.debug("Requete GET /api/candidats");
        return candidatService.getAllCandidats();
    }

    // Récupérer un candidat par ID
    @GetMapping("/{id}")
    public ResponseEntity<CandidatDto> getCandidatById(@PathVariable Long id) {
        log.debug("Requete GET /api/candidats/{}", id);
        return ResponseEntity.ok(candidatService.getCandidatById(id));
    }

    // Télécharger le CV d'un candidat
    @GetMapping("/{id}/cv")
    public ResponseEntity<byte[]> downloadCV(@PathVariable Long id) {
        log.debug("Requete GET /api/candidats/{}/cv", id);

        try {
            CandidatDto candidat = candidatService.getCandidatById(id);
            byte[] cvContent = candidatService.downloadCV(id);

            // Déterminer le type MIME en fonction de l'extension
            String mimeType = determineMimeType(candidat.getCvFileName());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(mimeType));
            headers.setContentDispositionFormData("inline",
                    URLEncoder.encode(candidat.getCvFileName(), StandardCharsets.UTF_8));

            return new ResponseEntity<>(cvContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Erreur lors du téléchargement du CV", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            log.error("CV non trouvé", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Mettre à jour un candidat avec possibilité de changer le CV
    @PutMapping("/{id}/with-cv")
    public ResponseEntity<?> updateCandidatWithCV(
            @PathVariable Long id,
            @RequestParam("nom") String nom,
            @RequestParam("email") String email,
            @RequestParam("competances") String competances,
            @RequestParam("anneesExperience") Integer anneesExperience,
            @RequestParam(value = "cv", required = false) MultipartFile cv) {

        log.info("Requete PUT /api/candidats/{}/with-cv", id);

        try {
            CandidatDto candidatDto = new CandidatDto();
            candidatDto.setNom(nom);
            candidatDto.setEmail(email);
            candidatDto.setCompetences(competances);
            candidatDto.setAnneesExperience(anneesExperience);

            CandidatDto updatedCandidat = candidatService.updateCandidatWithCV(id, candidatDto, cv);
            return ResponseEntity.ok(updatedCandidat);
        } catch (IOException e) {
            log.error("Erreur lors de la mise à jour du CV", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la mise à jour du CV: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // Supprimer un candidat
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCandidat(@PathVariable Long id) {
        log.info("Requete DELETE /api/candidats/{}", id);

        try {
            candidatService.deleteCandidat(id);
            return ResponseEntity.ok().body("Candidat supprimé avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    private String determineMimeType(String fileName) {
        if (fileName == null) return "application/octet-stream";

        if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.endsWith(".doc")) {
            return "application/msword";
        } else if (fileName.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
        return "application/octet-stream";
    }
}