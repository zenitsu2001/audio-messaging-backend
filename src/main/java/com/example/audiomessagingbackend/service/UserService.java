package com.example.audiomessagingbackend.service;

import com.example.audiomessagingbackend.model.User;
import com.example.audiomessagingbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des utilisateurs.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Enregistre un nouvel utilisateur.
     * @param user L'utilisateur à enregistrer.
     * @return L'utilisateur enregistré.
     */
    public User registerUser(User user) {
        // En production, nous hacherions le mot de passe ici avant de le sauvegarder.
        return userRepository.save(user);
    }

    /**
     * Valide les identifiants d'un utilisateur.
     * @param username Le nom d'utilisateur.
     * @param password Le mot de passe.
     * @return L'utilisateur si les identifiants sont valides, Optional.empty() sinon.
     */
    public Optional<User> validateUser(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password)); // Comparaison simple, à remplacer par un hachage en production
    }

    /**
     * Récupère un utilisateur par son ID.
     * @param id L'ID de l'utilisateur.
     * @return L'utilisateur si trouvé, Optional.empty() sinon.
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Récupère un utilisateur par son nom d'utilisateur.
     * @param username Le nom d'utilisateur.
     * @return L'utilisateur si trouvé, Optional.empty() sinon.
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Récupère tous les utilisateurs.
     * @return Une liste de tous les utilisateurs.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}