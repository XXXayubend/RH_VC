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
import org.springframework.test.web.servlet.MockMvc;
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

    @Test
    void testCreateCandidat_DuplicateEmail() throws Exception {
        // Premier candidat
        CandidatDto dto = new CandidatDto(null, "wisem", "wisem@email.com", "C++", 9);
        mockMvc.perform(post("/api/candidats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        // Second avec même email
        CandidatDto duplicate = new CandidatDto(null, "wasim", "wisem@email.com", "Python", 6);
        mockMvc.perform(post("/api/candidats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("existe deja")));
    }

    @Test
    void testCreateCandidat_ValidationError() throws Exception {
        CandidatDto invalid = new CandidatDto(null, "", "badEmail", "Java", -5);
        mockMvc.perform(post("/api/candidats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nom").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.anneesExperience").exists());
    }

    @Test
    void testGetAllCandidats() throws Exception {
        mockMvc.perform(get("/api/candidats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}