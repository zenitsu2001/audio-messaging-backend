package com.example.audiomessagingbackend.repository;

import com.example.audiomessagingbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour l'entité User.
 * Fournit des méthodes CRUD et des requêtes personnalisées pour les utilisateurs.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Méthode pour trouver un utilisateur par son nom d'utilisateur
    Optional<User> findByUsername(String username);
}