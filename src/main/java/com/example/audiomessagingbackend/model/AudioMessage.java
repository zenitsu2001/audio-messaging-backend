package com.example.audiomessagingbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité représentant un message audio envoyé entre utilisateurs.
 */
@Entity
@Table(name = "audio_messages")
@Data // Génère getters, setters, toString, equals et hashCode via Lombok
@NoArgsConstructor
@AllArgsConstructor
public class AudioMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrémenté
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Relation plusieurs messages vers un expéditeur
    @JoinColumn(name = "sender_id", nullable = false) // Colonne de la clé étrangère
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY) // Relation plusieurs messages vers un destinataire
    @JoinColumn(name = "receiver_id", nullable = false) // Colonne de la clé étrangère
    private User receiver;

    @Column(nullable = false)
    private String filePath; // Chemin local du fichier audio stocké

    @Column(nullable = false)
    private LocalDateTime timestamp; // Horodatage de l'envoi du message

    private String fileName; // Nom original du fichier pour référence

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}