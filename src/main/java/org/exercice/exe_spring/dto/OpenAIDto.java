package org.exercice.exe_spring.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAIDto {
    private Double score;
    private String analyse;
    private String recommandation;
    private String pointsForts;
    private String pointsFaibles;
    private String resume;
}
