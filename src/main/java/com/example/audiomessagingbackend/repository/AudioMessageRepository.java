package com.example.audiomessagingbackend.repository;

import com.example.audiomessagingbackend.model.AudioMessage;
import com.example.audiomessagingbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'entité AudioMessage.
 * Fournit des méthodes CRUD et des requêtes personnalisées pour les messages audio.
 */
@Repository
public interface AudioMessageRepository extends JpaRepository<AudioMessage, Long> {
    // Récupérer tous les messages envoyés ou reçus par un utilisateur donné (conversations)
    List<AudioMessage> findBySenderOrReceiverOrderByTimestampAsc(User sender, User receiver);

    // Récupérer les messages échangés entre deux utilisateurs spécifiques
    List<AudioMessage> findBySenderAndReceiverOrderByTimestampAsc(User sender, User receiver);

    List<AudioMessage> findBySenderAndReceiver(User sender, User receiver);

    List<AudioMessage> findByReceiverAndSender(User receiver, User sender);
}