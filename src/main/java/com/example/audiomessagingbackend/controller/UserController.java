package com.example.audiomessagingbackend.controller;

import com.example.audiomessagingbackend.model.User;
import com.example.audiomessagingbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des utilisateurs.
 */
@RestController
@RequestMapping("/api/users") // Préfixe de l'URL pour toutes les requêtes de ce contrôleur
@CrossOrigin(origins = "http://localhost:4200") // Permet les requêtes depuis l'application Angular sur localhost:4200
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Enregistre un nouvel utilisateur.
     * URL : POST /api/users/register
     * Corps de la requête : { "username": "...", "password": "..." }
     * @param user L'objet User reçu dans le corps de la requête.
     * @return ResponseEntity avec l'utilisateur enregistré et le statut HTTP 201 (Created).
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    /**
     * Authentifie un utilisateur.
     * URL : POST /api/users/login
     * Corps de la requête : { "username": "...", "password": "..." }
     * @param credentials Un Map contenant le nom d'utilisateur et le mot de passe.
     * @return ResponseEntity avec l'utilisateur authentifié et le statut HTTP 200 (OK), ou 401 (Unauthorized) si invalide.
     */
    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Optional<User> userOptional = userService.validateUser(username, password);

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Récupère tous les utilisateurs (pour la liste de contacts).
     * URL : GET /api/users
     * @return ResponseEntity avec une liste de tous les utilisateurs et le statut HTTP 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Récupère un utilisateur par son ID.
     * URL : GET /api/users/{id}
     * @param id L'ID de l'utilisateur.
     * @return ResponseEntity avec l'utilisateur et le statut HTTP 200 (OK), ou 404 (Not Found) si non trouvé.
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Récupère un utilisateur par son nom d'utilisateur.
     * URL : GET /api/users/username/{username}
     * @param username Le nom d'utilisateur.
     * @return ResponseEntity avec l'utilisateur et le statut HTTP 200 (OK), ou 404 (Not Found) si non trouvé.
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.getUserByUsername(username);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}