// src/main/java/com/example/audiomessagingbackend/service/AudioMessageService.java
package com.example.audiomessagingbackend.service;

import com.example.audiomessagingbackend.model.AudioMessage;
import com.example.audiomessagingbackend.model.User;
import com.example.audiomessagingbackend.repository.AudioMessageRepository;
import com.example.audiomessagingbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service pour la gestion des messages audio.
 */
@Service
public class AudioMessageService {

    @Autowired
    private AudioMessageRepository audioMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${file.upload-dir}") // Injecte la valeur de 'file.upload-dir' depuis application.properties
    private String uploadDir;

    private Path fileStorageLocation;

    /**
     * Initialise le répertoire de stockage des fichiers lors de la création du service.
     */
    public AudioMessageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    /**
     * Envoie un message audio.
     * @param senderId L'ID de l'expéditeur.
     * @param receiverId L'ID du destinataire.
     * @param audioFile Le fichier audio à envoyer.
     * @return Le message audio enregistré.
     * @throws IOException Si une erreur survient lors du stockage du fichier.
     */
    public AudioMessage sendAudioMessage(Long senderId, Long receiverId, MultipartFile audioFile) throws IOException {
        Optional<User> senderOptional = userRepository.findById(senderId);
        Optional<User> receiverOptional = userRepository.findById(receiverId);

        if (senderOptional.isEmpty() || receiverOptional.isEmpty()) {
            throw new IllegalArgumentException("Sender or Receiver not found.");
        }

        User sender = senderOptional.get();
        User receiver = receiverOptional.get();

        String originalFileName = audioFile.getOriginalFilename();
        // Générer un nom de fichier unique pour éviter les collisions
        // IMPORTANT: Assurez-vous d'avoir une extension de fichier, ex: .webm
        String fileExtension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < originalFileName.length() - 1) {
            fileExtension = originalFileName.substring(dotIndex);
        }
        String fileName = UUID.randomUUID().toString() + fileExtension; // Utilise seulement l'extension

        Path targetLocation = this.fileStorageLocation.resolve(fileName);

        // Copier le fichier dans le répertoire de stockage local
        Files.copy(audioFile.getInputStream(), targetLocation);

        AudioMessage audioMessage = new AudioMessage();
        audioMessage.setSender(sender);
        audioMessage.setReceiver(receiver);
        audioMessage.setFilePath(fileName); // <--- MODIFICATION ICI : On stocke seulement le nom du fichier
        audioMessage.setFileName(originalFileName);
        audioMessage.setTimestamp(LocalDateTime.now());

        return audioMessageRepository.save(audioMessage);
    }

    /**
     * Récupère les messages audio échangés entre deux utilisateurs.
     * @param user1Id L'ID du premier utilisateur.
     * @param user2Id L'ID du second utilisateur.
     * @return Une liste des messages audio triés par horodatage.
     */
    public List<AudioMessage> getConversation(Long user1Id, Long user2Id) {
        Optional<User> user1Optional = userRepository.findById(user1Id);
        Optional<User> user2Optional = userRepository.findById(user2Id);

        if (user1Optional.isEmpty() || user2Optional.isEmpty()) {
            return new ArrayList<>();
        }

        User user1 = user1Optional.get();
        User user2 = user2Optional.get();

        List<AudioMessage> messagesSent = audioMessageRepository.findBySenderAndReceiver(user1, user2);
        List<AudioMessage> messagesReceived = audioMessageRepository.findByReceiverAndSender(user1, user2);

        List<AudioMessage> conversation = new ArrayList<>();
        conversation.addAll(messagesSent);
        conversation.addAll(messagesReceived);

        conversation.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));

        return conversation;
    }

    /**
     * Charge le fichier audio à partir de son chemin.
     * @param fileName Le nom du fichier audio (peut inclure le UUID).
     * @return La ressource du fichier audio.
     * @throws MalformedURLException Si le chemin du fichier est mal formé.
     */
    public Resource loadAudioFileAsResource(String fileName) throws MalformedURLException {
        // La méthode resolve prend déjà le nom du fichier et le combine avec le répertoire de base
        Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists()) {
            return resource;
        } else {
            throw new RuntimeException("File not found " + fileName);
        }
    }
}