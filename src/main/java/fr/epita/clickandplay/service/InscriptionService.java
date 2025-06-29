package fr.epita.clickandplay.service;

import fr.epita.clickandplay.exception.*;
import fr.epita.clickandplay.model.*;
import fr.epita.clickandplay.repository.IInscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InscriptionService {

	private final IInscriptionRepository inscriptionRepository;
	private final TableService           tableService;
	private final UserService            userService;

	@Autowired
	public InscriptionService(IInscriptionRepository inscriptionRepository,
							  TableService tableService,
							  UserService userService) {
		this.inscriptionRepository = inscriptionRepository;
		this.tableService          = tableService;
		this.userService           = userService;
	}

	public void register(Long tableId) {

		Table table = tableService.getTableEntity(tableId);

		String username = getCurrentUsername();
		User user = userService.getUserEntity(username);

		if (!user.isContributor() && !user.isFirstSession())
			throw new ForbiddenException("Seuls les cotisants peuvent s’inscrire (hors première séance).");

		boolean alreadyInTable = table.getInscriptions().stream()
				.anyMatch(i -> i.getUser().getUsername().equals(username));
		if (alreadyInTable) throw new ForbiddenException("Déjà inscrit à cette table.");

		List<Inscription> inscriptions = inscriptionRepository.findByUserUsername(username);

		LocalDateTime start = table.getStartTime();
		LocalDateTime end   = start.plusMinutes(table.getDuration());

		boolean overlap = inscriptions.stream().anyMatch(insc -> {
			Table other = insc.getTable();
			LocalDateTime oStart = other.getStartTime();
			LocalDateTime oEnd   = oStart.plusMinutes(other.getDuration());
			return start.isBefore(oEnd) && oStart.isBefore(end);
		});
		if (overlap) throw new ConflictException("Conflit avec une autre table.");

		if (table.getInscriptions().size() >= table.getMaxPlayers())
			throw new ForbiddenException("Cette table est déjà pleine.");

		Inscription insc = new Inscription();
		insc.setUser(user);
		insc.setTable(table);
		inscriptionRepository.save(insc);

		if (user.isFirstSession()) {
			user.setFirstSession(false);
			userService.saveUser(user);
		}
		System.out.println("[INFO] " + username + " s’est inscrit à " + table.getGameName());
	}

	public void unregister(Long tableId) {
		String username = getCurrentUsername();
		Inscription insc = inscriptionRepository.findByUserUsernameAndTableId(username, tableId)
				.orElseThrow(() -> new NotFoundException("Inscription introuvable."));
		inscriptionRepository.delete(insc);
		System.out.println("[INFO] " + username + " s’est désinscrit de la table ID " + tableId);
	}

	private String getCurrentUsername() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth.getName();
	}
}
