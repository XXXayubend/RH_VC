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

    private final Path baseUploadDir;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM");

    public FileStorageService(@Value("${file.upload-dir:uploads/cvs}") String uploadDir) {
        this.baseUploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.baseUploadDir);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create base upload directory", ex);
        }
    }

    public String storeFile(MultipartFile file) throws IOException {
        validateFile(file);

        // Create date-based subdirectory
        String datePath = LocalDateTime.now().format(DATE_FORMATTER);
        Path targetDir = this.baseUploadDir.resolve(datePath);
        Files.createDirectories(targetDir);

        String fileName = generateUniqueFileName(file);
        Path targetLocation = targetDir.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return targetLocation.toString();
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("CV file is required");
        }
        String contentType = file.getContentType();
        if (!isValidFileType(contentType)) {
            throw new RuntimeException("Unsupported file type. Use PDF, DOC or DOCX");
        }
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new RuntimeException("File must not exceed 5MB");
        }
    }

    private String generateUniqueFileName(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("cv_%s_%s%s", timestamp, UUID.randomUUID().toString().substring(0, 8), fileExtension);
    }

    public boolean fileExists(String filePath) {
        if (filePath == null || filePath.isEmpty()) return false;
        Path path = Paths.get(filePath);
        return Files.exists(path) && Files.isRegularFile(path);
    }

    public byte[] readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        // Security: ensure path is inside base directory
        if (!path.normalize().startsWith(baseUploadDir)) {
            throw new SecurityException("Access denied to file outside upload directory");
        }
        return Files.readAllBytes(path);
    }

    private boolean isValidFileType(String contentType) {
        return contentType != null && (
                contentType.equals("application/pdf") ||
                        contentType.equals("application/msword") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        );
    }
}