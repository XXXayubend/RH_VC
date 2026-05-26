package org.exercice.exe_spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.exercice.exe_spring.dto.CandidatDto;
import org.exercice.exe_spring.dto.OffreDto;
import org.exercice.exe_spring.dto.AIDto;
import org.exercice.exe_spring.repository.CandidatRepository;
import org.exercice.exe_spring.repository.OffreRepository;
import org.exercice.exe_spring.service.AIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OpenAIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private OffreRepository offreRepository;

    @MockBean
    private AIService openAIService;

    private Long testCandidatId;
    private Long testOffreId;

    @BeforeEach
    void setUp() throws Exception {
        candidatRepository.deleteAll();
        offreRepository.deleteAll();

        // Créer un candidat de test
        CandidatDto candidatDto = new CandidatDto(null, "Test Candidat", "test@email.com",
                "Java, Spring, Microservices", 5);
        String candidatResponse = mockMvc.perform(post("/api/candidats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(candidatDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CandidatDto createdCandidat = objectMapper.readValue(candidatResponse, CandidatDto.class);
        testCandidatId = createdCandidat.getId();

        // Créer une offre de test
        OffreDto offreDto = new OffreDto(null, "Backend Developer", "Java, Spring, SQL, Microservices");
        String offreResponse = mockMvc.perform(post("/api/offres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(offreDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        OffreDto createdOffre = objectMapper.readValue(offreResponse, OffreDto.class);
        testOffreId = createdOffre.getId();

        // Configuration du mock
        AIDto mockResponse = new AIDto();
        mockResponse.setScore(85.0);
        mockResponse.setAnalyse("Très bon matching entre les compétences");
        mockResponse.setRecommandation("RECOMMANDE");
        mockResponse.setPointsForts("Excellente maîtrise de Java et Spring");
        mockResponse.setPointsFaibles("Manque d'expérience en cloud");
        mockResponse.setResume("Profil recommandé pour ce poste");

        when(openAIService.evaluateMatch(any(Long.class), any(Long.class))).thenReturn(mockResponse);
    }

    @Test
    void testEvaluateMatch() throws Exception {
        mockMvc.perform(post("/api/ai/evaluate/{candidatId}/{offreId}", testCandidatId, testOffreId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(85.0))
                .andExpect(jsonPath("$.analyse").value("Très bon matching entre les compétences"))
                .andExpect(jsonPath("$.recommandation").value("RECOMMANDE"));
    }

    @Test
    void testGetSimpleScore() throws Exception {
        mockMvc.perform(get("/api/ai/score/{candidatId}/{offreId}", testCandidatId, testOffreId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(85.0))
                .andExpect(jsonPath("$.recommandation").value("RECOMMANDE"));
    }
}