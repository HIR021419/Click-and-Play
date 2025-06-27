package fr.epita.clickandplay.controller;

import fr.epita.clickandplay.dto.UserDto;
import fr.epita.clickandplay.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Récupère les infos de l'utilisateur connecté
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    /**
     * Effectue une cotisation (une seule fois autorisée)
     */
    @PostMapping("/cotiser")
    public ResponseEntity<String> contribute() {
        String message = userService.contribute();
        return ResponseEntity.ok(message);
    }
}
