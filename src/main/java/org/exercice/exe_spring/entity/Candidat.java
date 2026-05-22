package org.exercice.exe_spring.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;  // ← AJOUTEZ CET IMPORT
import jakarta.persistence.PrePersist;

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
    private String competences;

    @Column(name = "annees_experience")
    private Integer experience;

    @Column(name = "cv_file_name")
    private String cvFileName;

    @Column(name = "cv_path")
    private String cvPath;

    @Column(name = "cv_mime_type")
    private String cvMimeType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}