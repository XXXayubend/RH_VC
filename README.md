# Gestion des Candidats et Offres - API REST Spring Boot

## Description

Ce projet est une application backend Spring Boot qui expose deux ressources REST :
- **Candidats** : création et consultation de profils (nom, email, compétences, expérience)
- **Offres** : création et consultation d'offres (titre, compétences requises)

L'application suit une architecture classique en couches :  
`Contrôleur → Service → Repository` avec utilisation de DTO, mappers, validation des données, logs structurés et gestion centralisée des exceptions.

## Fonctionnalités

- ✅ Création d'un candidat (POST `/api/candidats`) avec validation des champs
- ✅ Liste de tous les candidats (GET `/api/candidats`)
- ✅ Création d'une offre (POST `/api/offres`) avec validation
- ✅ Liste de toutes les offres (GET `/api/offres`)
- ✅ Vérification d'unicité de l'email pour les candidats (exception personnalisée)
- ✅ Gestion globale des exceptions (`@RestControllerAdvice`) avec statuts HTTP appropriés
- ✅ Logs détaillés (niveaux INFO, DEBUG, ERROR)
- ✅ Tests d'intégration des endpoints (Postman)

## Technologies utilisées

- Java 17+
- Spring Boot 3.x
- Spring Data JPA (Hibernate)
- Base de données H2 (ou MySQL)
- Lombok (réduction du code boilerplate)
- Jakarta Validation (validation des DTO)
- Maven (gestion des dépendances)

## Prérequis

- JDK 17 ou supérieur
- Maven 3.6+
- Un IDE (IntelliJ, Eclipse, VS Code) ou un terminal

## Installation et exécution

1. **Cloner le dépôt** (ou copier les sources) :
   ```bash
   git clone https://github.com/votre-repo/exe_spring.git
   cd exe_spring