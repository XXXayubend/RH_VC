package org.exercice.exe_spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.exercice.exe_spring.dto.CandidatDto;
import org.exercice.exe_spring.repository.CandidatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CandidatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CandidatRepository candidatRepository;

    @BeforeEach
    void clean() {
        candidatRepository.deleteAll();
    }

    // Test - Créer candidat SANS CV
    @Test
    void testCreateCandidat_Success() throws Exception {
        CandidatDto dto = new CandidatDto(null, "wafa", "wafa@email.com", "Java,Spring", 3);
        mockMvc.perform(post("/api/candidats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("wafa@email.com"))
                .andExpect(jsonPath("$.nom").value("wafa"));
    }

    // Test - Créer candidat AVEC CV
    @Test
    void testCreateCandidatWithCV_Success() throws Exception {
        MockMultipartFile cvFile = new MockMultipartFile(
                "cv",
                "cv_test.pdf",
                "application/pdf",
                "Contenu du CV factice".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/candidats/with-cv")
                        .file(cvFile)
                        .param("nom", "Ahmed Ben Ali")
                        .param("email", "ahmed@email.com")
                        .param("competances", "Java, Spring Boot, Angular")
                        .param("anneesExperience", "5"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Ahmed Ben Ali"))
                .andExpect(jsonPath("$.email").value("ahmed@email.com"))
                .andExpect(jsonPath("$.cvFileName").value("cv_test.pdf"));
    }

    // Test - Créer candidat avec CV mais sans fichier (doit échouer)
    @Test
    void testCreateCandidatWithCV_NoFile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/candidats/with-cv")
                        .param("nom", "Sami Ben Salah")
                        .param("email", "sami@email.com")
                        .param("competances", "Python, Django")
                        .param("anneesExperience", "3"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("CV est obligatoire")));
    }

    // Test - Créer candidat avec type de fichier invalide
    @Test
    void testCreateCandidatWithCV_InvalidFileType() throws Exception {
        MockMultipartFile cvFile = new MockMultipartFile(
                "cv",
                "cv_test.txt",
                "text/plain",
                "Contenu texte".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/candidats/with-cv")
                        .file(cvFile)
                        .param("nom", "Nadia Ben Ali")
                        .param("email", "nadia@email.com")
                        .param("competances", "React, Node.js")
                        .param("anneesExperience", "4"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Type de fichier non supporté")));
    }

    // Test - Email dupliqué
    @Test
    void testCreateCandidat_DuplicateEmail() throws Exception {
        CandidatDto dto = new CandidatDto(null, "wisem", "wisem@email.com", "C++", 9);
        mockMvc.perform(post("/api/candidats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        CandidatDto duplicate = new CandidatDto(null, "wasim", "wisem@email.com", "Python", 6);
        mockMvc.perform(post("/api/candidats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("existe deja")));
    }

    // Test - Validation erreur
    @Test
    void testCreateCandidat_ValidationError() throws Exception {
        CandidatDto invalid = new CandidatDto(null, "", "badEmail", "Java", -5);
        mockMvc.perform(post("/api/candidats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    // Test - Lister tous les candidats
    @Test
    void testGetAllCandidats() throws Exception {
        mockMvc.perform(get("/api/candidats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // Test - Récupérer un candidat par ID
    @Test
    void testGetCandidatById_Success() throws Exception {
        CandidatDto dto = new CandidatDto(null, "Mohamed Ali", "mohamed@email.com", "Java", 5);
        String response = mockMvc.perform(post("/api/candidats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CandidatDto created = objectMapper.readValue(response, CandidatDto.class);

        mockMvc.perform(get("/api/candidats/{id}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.email").value("mohamed@email.com"));
    }

    // Test - Récupérer candidat qui n'existe pas
    @Test
    void testGetCandidatById_NotFound() throws Exception {
        mockMvc.perform(get("/api/candidats/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    // Test - Télécharger le CV
    @Test
    void testDownloadCV_Success() throws Exception {
        MockMultipartFile cvFile = new MockMultipartFile(
                "cv",
                "mon_cv.pdf",
                "application/pdf",
                "Contenu du CV".getBytes(StandardCharsets.UTF_8)
        );

        String response = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/candidats/with-cv")
                        .file(cvFile)
                        .param("nom", "Karim Ben Ali")
                        .param("email", "karim@email.com")
                        .param("competances", "DevOps, Cloud")
                        .param("anneesExperience", "7"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CandidatDto created = objectMapper.readValue(response, CandidatDto.class);

        mockMvc.perform(get("/api/candidats/{id}/cv", created.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().exists("Content-Disposition"));
    }

    // Test - Télécharger CV d'un candidat qui n'existe pas
    @Test
    void testDownloadCV_CandidatNotFound() throws Exception {
        mockMvc.perform(get("/api/candidats/{id}/cv", 9999L))
                .andExpect(status().isNotFound());
    }

    // Test - Mettre à jour un candidat avec nouveau CV
    @Test
    void testUpdateCandidatWithCV_Success() throws Exception {
        MockMultipartFile oldCv = new MockMultipartFile(
                "cv",
                "ancien_cv.pdf",
                "application/pdf",
                "Ancien CV".getBytes(StandardCharsets.UTF_8)
        );

        String response = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/candidats/with-cv")
                        .file(oldCv)
                        .param("nom", "Hatem Ben Ali")
                        .param("email", "hatem@email.com")
                        .param("competances", "Java")
                        .param("anneesExperience", "2"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CandidatDto created = objectMapper.readValue(response, CandidatDto.class);

        MockMultipartFile newCv = new MockMultipartFile(
                "cv",
                "nouveau_cv.pdf",
                "application/pdf",
                "Nouveau CV".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/candidats/{id}/with-cv", created.getId())
                        .file(newCv)
                        .param("nom", "Hatem Ben Ali Updated")
                        .param("email", "hatem.updated@email.com")
                        .param("competances", "Java, Spring, Angular")
                        .param("anneesExperience", "3")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Hatem Ben Ali Updated"))
                .andExpect(jsonPath("$.email").value("hatem.updated@email.com"))
                .andExpect(jsonPath("$.competences").value("Java, Spring, Angular"));
    }

    // Test - Supprimer un candidat
    @Test
    void testDeleteCandidat_Success() throws Exception {
        CandidatDto dto = new CandidatDto(null, "Fathi Ben Salah", "fathi@email.com", "PHP", 4);
        String response = mockMvc.perform(post("/api/candidats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CandidatDto created = objectMapper.readValue(response, CandidatDto.class);

        mockMvc.perform(delete("/api/candidats/{id}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("supprimé avec succès")));

        mockMvc.perform(get("/api/candidats/{id}", created.getId()))
                .andExpect(status().isNotFound());
    }

    // Test - Supprimer un candidat qui n'existe pas
    @Test
    void testDeleteCandidat_NotFound() throws Exception {
        mockMvc.perform(delete("/api/candidats/{id}", 9999L))
                .andExpect(status().isNotFound());
    }
}