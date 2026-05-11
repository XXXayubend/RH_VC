package org.exercice.exe_spring.entity;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "candidat")
public class Candidat {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_complet")
    private String nom;

    @Column(name = "email_id", nullable = false, unique = true)
    private String email;

    @Column(name = "competences")
    private String competence;

    @Column(name = "annees_experience")
    private Integer experience;
}
