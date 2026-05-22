package org.exercice.exe_spring.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exercice.exe_spring.dto.CandidatDto;
import org.exercice.exe_spring.entity.Candidat;
import org.exercice.exe_spring.exception.DuplicateEmailException;
import org.exercice.exe_spring.exception.ResourceNotFoundException;
import org.exercice.exe_spring.mapper.CandidatMapper;
import org.exercice.exe_spring.repository.CandidatRepository;
import org.exercice.exe_spring.service.CandidatService;
import org.exercice.exe_spring.service.FileStorageService;
import org.exercice.exe_spring.service.MatchingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CandidatServiceImpl implements CandidatService {

    private final CandidatRepository candidatRepository;
    private final FileStorageService fileStorageService;
    private final MatchingService matchingService;

    @Override
    public CandidatDto createCandidat(CandidatDto candidatDto) {
        log.debug("Tentative de création candidat: {}", candidatDto.getEmail());

        if (candidatRepository.findByEmail(candidatDto.getEmail()).isPresent()) {
            log.error("Email deja utilisée: {}", candidatDto.getEmail());
            throw new DuplicateEmailException("Un candidat avec cet email existe deja : " + candidatDto.getEmail());
        }

        Candidat candidat = CandidatMapper.mapToCandidat(candidatDto);
        Candidat savedCandidat = candidatRepository.save(candidat);
        log.info("Candidat sauvegarder avec id {}", savedCandidat.getId());
        return CandidatMapper.mapToCandidatDto(savedCandidat);
    }

    @Override
    @Transactional
    public CandidatDto createCandidatWithCV(CandidatDto candidatDto, MultipartFile file) throws Exception {
        log.debug("Tentative de création candidat avec CV: {}", candidatDto.getEmail());

        // Vérifier si l'email existe déjà
        if (candidatRepository.findByEmail(candidatDto.getEmail()).isPresent()) {
            log.error("Email deja utilisée: {}", candidatDto.getEmail());
            throw new DuplicateEmailException("Un candidat avec cet email existe deja : " + candidatDto.getEmail());
        }

        // Vérifier le fichier
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Le CV est obligatoire");
        }

        // Stocker le fichier
        String filePath = fileStorageService.storeFile(file);

        // Créer l'entité
        Candidat candidat = CandidatMapper.mapToCandidat(candidatDto);
        candidat.setCvFileName(file.getOriginalFilename());
        candidat.setCvPath(filePath);
        candidat.setCvMimeType(file.getContentType());

        Candidat savedCandidat = candidatRepository.save(candidat);
        log.info("Candidat avec CV sauvegardé avec id {}", savedCandidat.getId());

        String extractedText = matchingService.analyzeAndSaveForCandidat(savedCandidat.getId(), file);
        log.info("Texte du CV extrait et sauvegardé ({} caractères)", extractedText.length());

        return CandidatMapper.mapToCandidatDto(savedCandidat);
    }

    @Override
    public List<CandidatDto> getAllCandidats() {
        log.debug("Récupération de tous les candidats");
        return candidatRepository.findAll()
                .stream()
                .map(CandidatMapper::mapToCandidatDto)
                .collect(Collectors.toList());
    }

    @Override
    public CandidatDto getCandidatById(Long id) {
        Candidat candidat = getCandidatEntityById(id);
        return CandidatMapper.mapToCandidatDto(candidat);
    }

    @Override
    public byte[] downloadCV(Long id) throws IOException {
        Candidat candidat = getCandidatEntityById(id);

        if (candidat.getCvPath() == null || candidat.getCvPath().isEmpty()) {
            throw new RuntimeException("CV non trouvé pour ce candidat");
        }

        if (!fileStorageService.fileExists(candidat.getCvPath())) {
            throw new RuntimeException("Fichier CV introuvable sur le serveur");
        }

        return fileStorageService.readFile(candidat.getCvPath());
    }

    @Override
    @Transactional
    public CandidatDto updateCandidatWithCV(Long id, CandidatDto candidatDto, MultipartFile file) throws IOException {
        log.debug("Mise à jour du candidat avec id: {}", id);

        Candidat candidat = getCandidatEntityById(id);

        // Mettre à jour les informations
        candidat.setNom(candidatDto.getNom());
        candidat.setEmail(candidatDto.getEmail());
        candidat.setCompetences(candidatDto.getCompetences());
        candidat.setExperience(candidatDto.getAnneesExperience());

        // Si nouveau CV fourni
        if (file != null && !file.isEmpty()) {
            // Supprimer l'ancien CV
            if (candidat.getCvPath() != null) {
                fileStorageService.deleteFile(candidat.getCvPath());
            }

            // Stocker le nouveau CV
            String filePath = fileStorageService.storeFile(file);
            candidat.setCvFileName(file.getOriginalFilename());
            candidat.setCvPath(filePath);
            candidat.setCvMimeType(file.getContentType());
        }

        Candidat updatedCandidat = candidatRepository.save(candidat);
        log.info("Candidat mis à jour avec succès, id: {}", id);

        return CandidatMapper.mapToCandidatDto(updatedCandidat);
    }

    @Override
    @Transactional
    public void deleteCandidat(Long id) {
        log.debug("Suppression du candidat avec id: {}", id);

        Candidat candidat = getCandidatEntityById(id);

        // Supprimer le fichier CV
        if (candidat.getCvPath() != null) {
            fileStorageService.deleteFile(candidat.getCvPath());
            log.debug("Fichier CV supprimé: {}", candidat.getCvPath());
        }

        candidatRepository.deleteById(id);
        log.info("Candidat supprimé avec succès, id: {}", id);
    }

    private Candidat getCandidatEntityById(Long id) {
        return candidatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidat non trouvé avec l'id: " + id));
    }
}