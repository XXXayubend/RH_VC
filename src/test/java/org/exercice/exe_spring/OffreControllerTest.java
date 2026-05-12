package org.exercice.exe_spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.exercice.exe_spring.dto.OffreDto;
import org.exercice.exe_spring.repository.OffreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import static org.hamcrest.Matchers.containsString;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OffreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OffreRepository offreRepository;

    @BeforeEach
    void clean() {
        offreRepository.deleteAll();
    }


    @Test
    void testCreateOffre_Syccess() throws Exception {
        OffreDto dto = new OffreDto(null, "BackEnd developper", "python3");
        mockMvc.perform(post("/api/offres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titre").value("BackEnd developper"));
    }

    @Test
    void testCreateOffre_DuplicateTitre() throws Exception {
        // Premier offre
        OffreDto dto = new OffreDto(null, "data engineer", "Python");
        mockMvc.perform(post("/api/offres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        // Second oddre
        OffreDto duplicate = new OffreDto(null, "data engineer", "Python");
        mockMvc.perform(post("/api/offres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict())
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("exist deja")));
    }

    @Test
    void testCreateOffre_ValidationError() throws Exception {
        OffreDto invalid = new OffreDto(null, "Manager Senior","");
        mockMvc.perform(post("/api/offres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.titre").exists())
                .andExpect(jsonPath("$.copetancesRequises").exists());
    }

    @Test
    void testGetAllOffres() throws Exception {
        mockMvc.perform(get("/api/offres"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
