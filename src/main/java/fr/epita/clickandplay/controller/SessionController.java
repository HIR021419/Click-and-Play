package fr.epita.clickandplay.controller;

import fr.epita.clickandplay.dto.SessionDto;
import fr.epita.clickandplay.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    @Autowired
    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    /**
     * Récupère toutes les sessions à venir
     */
    @GetMapping
    public ResponseEntity<List<SessionDto>> getAllUpcomingSessions() {
        return ResponseEntity.ok(sessionService.getAllFutureSessions());
    }

    /**
     * Crée une nouvelle session (réservé à l'administrateur)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SessionDto> createSession(@RequestBody SessionDto dto) {
        return new ResponseEntity<>(sessionService.createSession(dto), HttpStatus.CREATED);
    }

    /**
     * Supprime une session (et notifie les participants)
     */
    @DeleteMapping("/{sessionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSession(@PathVariable Long sessionId) {
        sessionService.deleteSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}
