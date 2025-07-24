package com.example.audiomessagingbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;

/**
 * Entité représentant un utilisateur dans l'application.
 */
@Entity
@Table(name = "users")
@Data // Génère getters, setters, toString, equals et hashCode via Lombok
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // Le nom d'utilisateur doit être unique et non nul
    private String username;

    @Column(nullable = false)
    private String password; // En production, le mot de passe devrait être haché et salé

    // Les messages envoyés par cet utilisateur
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<AudioMessage> sentMessages;

    // Les messages reçus par cet utilisateur
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<AudioMessage> receivedMessages;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<AudioMessage> getSentMessages() {
        return sentMessages;
    }

    public void setSentMessages(Set<AudioMessage> sentMessages) {
        this.sentMessages = sentMessages;
    }

    public Set<AudioMessage> getReceivedMessages() {
        return receivedMessages;
    }

    public void setReceivedMessages(Set<AudioMessage> receivedMessages) {
        this.receivedMessages = receivedMessages;
    }
}