package org.exercice.exe_spring.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "candidat")
@Data
public class Candidature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_complet")
    private String name;

    @Column(name = "email_id", nullable = false, unique = true)
    private String email;

    @Column(name = "competences")
    private String competence;

    @Column(name = "annees_experience")
    private Integer experience;
}
