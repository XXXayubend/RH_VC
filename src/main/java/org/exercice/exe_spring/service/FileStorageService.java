package org.exercice.exe_spring.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    // Organisation des fichiers par année/mois pour éviter d'avoir trop de fichiers dans un dossier
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM");

    public FileStorageService(@Value("${file.upload-dir:uploads/cvs}") String uploadDir) {
        // Création d'un chemin organisé par date
        String datedPath = LocalDateTime.now().format(DATE_FORMATTER);
        Path basePath = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.fileStorageLocation = basePath.resolve(datedPath);

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Impossible de créer le répertoire d'upload: " + this.fileStorageLocation, ex);
        }
    }

    /**
     * Stocke un fichier CV avec un nom unique
     * @param file - Fichier à stocker
     * @return Chemin complet du fichier stocké
     * @throws IOException - Erreur lors de l'écriture du fichier
     */
    public String storeFile(MultipartFile file) throws IOException {
        validateFile(file);

        // Génération d'un nom unique pour éviter les collisions
        String fileName = generateUniqueFileName(file);

        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return targetLocation.toString();
    }

    /**
     * Valide le fichier avant stockage
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Le CV est obligatoire");
        }

        String contentType = file.getContentType();
        if (!isValidFileType(contentType)) {
            throw new RuntimeException("Type de fichier non supporté. Utilisez PDF, DOC ou DOCX");
        }

        // Limite de taille : 5MB
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new RuntimeException("Le fichier ne doit pas dépasser 5MB");
        }
    }

    /**
     * Génère un nom de fichier unique
     */
    private String generateUniqueFileName(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";

        if (originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        // Format: cv_YYYYMMDD_HHMMSS_UUID.extension
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("cv_%s_%s%s", timestamp, UUID.randomUUID().toString().substring(0, 8), fileExtension);
    }

    /**
     * Supprime un fichier CV
     */
    public void deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) return;

        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);

            // Suppression du répertoire parent s'il est vide (optionnel)
            Path parent = path.getParent();
            if (parent != null && isDirectoryEmpty(parent)) {
                Files.deleteIfExists(parent);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Impossible de supprimer le fichier: " + filePath, ex);
        }
    }

    /**
     * Vérifie si un répertoire est vide
     */
    private boolean isDirectoryEmpty(Path directory) throws IOException {
        try (var stream = Files.list(directory)) {
            return !stream.findFirst().isPresent();
        }
    }

    /**
     * Vérifie l'existence d'un fichier
     */
    public boolean fileExists(String filePath) {
        if (filePath == null || filePath.isEmpty()) return false;
        return Files.exists(Paths.get(filePath));
    }

    /**
     * Lit le contenu d'un fichier
     */
    public byte[] readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }

    /**
     * Valide le type MIME du fichier
     */
    private boolean isValidFileType(String contentType) {
        return contentType != null && (
                contentType.equals("application/pdf") ||
                        contentType.equals("application/msword") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        );
    }
}