package org.exercice.exe_spring.service;

//import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class TextExtractService {

    public String extractTextFromCV(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();

        if (fileName == null) {
            throw new Exception("Fichier invalide");
        }
            
        if (fileName.toLowerCase().endsWith(".pdf")) {
            return extractFromPDF(file);
        } else if (fileName.toLowerCase().endsWith(".docx")) {
            return extractFromDOCX(file);
        } else if (fileName.toLowerCase().endsWith(".doc")) {
            throw new Exception("Format DOC non supporté. Veuillez convertir en PDF ou DOCX");
        } else {
            throw new Exception("Format non supporté. Utilisez PDF ou DOCX");
        }
    }

    private String extractFromPDF(MultipartFile file) throws Exception {
        try (InputStream inputStream = file.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractFromDOCX(MultipartFile file) throws Exception {
        try (InputStream inputStream = file.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }
}