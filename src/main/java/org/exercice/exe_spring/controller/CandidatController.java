package org.exercice.exe_spring.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exercice.exe_spring.dto.CandidatDto;
import org.exercice.exe_spring.service.CandidatService;
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
@RestController
@RequestMapping("/api/candidats")
public class CandidatController {

    private final CandidatService candidatService;

    @PostMapping
    public ResponseEntity<CandidatDto> createCandidat(@Valid @RequestBody CandidatDto candidatDto){
        log.info("POST /api/candidats - body: {}", candidatDto);
        CandidatDto saved = candidatService.createCandidat(candidatDto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PostMapping("/with-cv")
    public ResponseEntity<?> createCandidatWithCV(
            @RequestParam("nom") String nom,
            @RequestParam("email") String email,
            @RequestParam("competances") String competences,
            @RequestParam("anneesExperience") Integer anneesExperience,
            @RequestParam("cv") MultipartFile cv) {

        log.info("POST /api/candidats/with-cv - nom: {}, email: {}", nom, email);
        try {
            CandidatDto dto = new CandidatDto();
            dto.setNom(nom);
            dto.setEmail(email);
            dto.setCompetences(competences);
            dto.setAnneesExperience(anneesExperience);

            CandidatDto saved = candidatService.createCandidatWithCV(dto, cv);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (IOException e) {
            log.error("File storage error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving CV: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("Business error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }

    @GetMapping
    public List<CandidatDto> getAllCandidats() {
        return candidatService.getAllCandidats();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidatDto> getCandidatById(@PathVariable Long id) {
        return ResponseEntity.ok(candidatService.getCandidatById(id));
    }

    @GetMapping("/{id}/cv")
    public ResponseEntity<byte[]> downloadCV(@PathVariable Long id) {
        try {
            CandidatDto candidat = candidatService.getCandidatById(id);
            byte[] cvContent = candidatService.downloadCV(id);
            String mimeType = determineMimeType(candidat.getCvFileName());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(mimeType));
            headers.setContentDispositionFormData("inline",
                    URLEncoder.encode(candidat.getCvFileName(), StandardCharsets.UTF_8));
            return new ResponseEntity<>(cvContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Error reading CV file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            log.warn("CV not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private String determineMimeType(String fileName) {
        if (fileName == null) return "application/octet-stream";
        if (fileName.endsWith(".pdf")) return "application/pdf";
        if (fileName.endsWith(".doc")) return "application/msword";
        if (fileName.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        return "application/octet-stream";
    }
}