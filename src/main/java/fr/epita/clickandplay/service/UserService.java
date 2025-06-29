package fr.epita.clickandplay.service;

import fr.epita.clickandplay.dto.UserDto;
import fr.epita.clickandplay.exception.*;
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

	public User getUserEntity(String username) {
		return userRepository.findById(username)
				.orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));
	}

	public void saveUser(User user) {
		userRepository.save(user);
	}

	public UserDto getCurrentUser() {
		return new UserDto(getUserEntity(getConnectedUsername()));
	}

	public String contribute() {
		User user = getUserEntity(getConnectedUsername());
		if (user.isContributor()) throw new BadRequestException("L'utilisateur a déjà cotisé.");

		user.setContributor(true);
		saveUser(user);
		System.out.println("[API COTISATION] " + user.getUsername() + " vient de cotiser.");
		return "Cotisation enregistrée.";
	}

	/* ----------- helpers ----------- */
	private String getConnectedUsername() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth.getName();
	}
}
