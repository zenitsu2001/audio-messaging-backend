package com.example.audiomessagingbackend.controller;

import com.example.audiomessagingbackend.model.AudioMessage;
import com.example.audiomessagingbackend.service.AudioMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des messages audio.
 */
@RestController
@RequestMapping("/api/messages") // Préfixe de l'URL pour toutes les requêtes de ce contrôleur
@CrossOrigin(origins = "http://localhost:4200") // Permet les requêtes depuis l'application Angular sur localhost:4200
public class AudioMessageController {

    @Autowired
    private AudioMessageService audioMessageService;

    /**
     * Envoie un message audio.
     * URL : POST /api/messages/send
     * Requête : form-data avec 'senderId', 'receiverId' et 'audioFile'
     * @param senderId L'ID de l'expéditeur.
     * @param receiverId L'ID du destinataire.
     * @param audioFile Le fichier audio (MultipartFile).
     * @return ResponseEntity avec le message audio enregistré et le statut HTTP 201 (Created).
     */
    @PostMapping("/send")
    public ResponseEntity<AudioMessage> sendAudioMessage(
            @RequestParam("senderId") Long senderId,
            @RequestParam("receiverId") Long receiverId,
            @RequestParam("audioFile") MultipartFile audioFile) {
        try {
            AudioMessage sentMessage = audioMessageService.sendAudioMessage(senderId, receiverId, audioFile);
            return new ResponseEntity<>(sentMessage, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Récupère la conversation (messages audio) entre deux utilisateurs.
     * URL : GET /api/messages/conversation/{user1Id}/{user2Id}
     * @param user1Id L'ID du premier utilisateur.
     * @param user2Id L'ID du second utilisateur.
     * @return ResponseEntity avec la liste des messages audio et le statut HTTP 200 (OK).
     */
    @GetMapping("/conversation/{user1Id}/{user2Id}")
    public ResponseEntity<List<AudioMessage>> getConversation(
            @PathVariable Long user1Id,
            @PathVariable Long user2Id) {
        List<AudioMessage> conversation = audioMessageService.getConversation(user1Id, user2Id);
        return ResponseEntity.ok(conversation);
    }

    /**
     * Télécharge un fichier audio.
     * URL : GET /api/messages/download/{fileName}
     * @param fileName Le nom du fichier audio à télécharger (peut inclure le UUID).
     * @return ResponseEntity avec la ressource du fichier audio et les en-têtes appropriés.
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadAudioFile( @RequestParam(value = "fileName") String fileName) {
        try {
            Resource resource = audioMessageService.loadAudioFileAsResource(fileName);
            // Déterminer le type de média (MIME type)
            String contentType = "audio/mpeg"; // Ou "audio/wav", "audio/ogg", etc., en fonction de vos fichiers
            // Vous pourriez vouloir déterminer le contentType plus dynamiquement en fonction de l'extension du fichier

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/download2")
    public ResponseEntity<Resource> download(@RequestParam String fileName) throws MalformedURLException {
        Path filePath = Paths.get("uploads/audio").resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}