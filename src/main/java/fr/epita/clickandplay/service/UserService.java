package fr.epita.clickandplay.service;

import fr.epita.clickandplay.dto.UserDto;
import fr.epita.clickandplay.exception.BadRequestException;
import fr.epita.clickandplay.exception.ConflictException;
import fr.epita.clickandplay.exception.NotFoundException;
import fr.epita.clickandplay.model.User;
import fr.epita.clickandplay.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final IUserRepository userRepository;

    @Autowired
    public UserService(IUserRepository repo) {
        this.userRepository = repo;
    }

    public UserDto getCurrentUser() {
        String username = getConnectedUsername();
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));
        return new UserDto(user);
    }

    public String contribute() {
        String username = getConnectedUsername();
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        if (user.isContributor()) {
            throw new BadRequestException("L'utilisateur a déjà cotisé.");
        }

        user.setContributor(true);
        userRepository.save(user);

        System.out.println("[API COTISATION] " + username + " vient de cotiser.");

        return "Cotisation enregistrée.";
    }

    private String getConnectedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
