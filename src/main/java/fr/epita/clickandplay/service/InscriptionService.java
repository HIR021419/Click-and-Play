package fr.epita.clickandplay.service;

import fr.epita.clickandplay.exception.ConflictException;
import fr.epita.clickandplay.exception.ForbiddenException;
import fr.epita.clickandplay.exception.NotFoundException;
import fr.epita.clickandplay.model.Inscription;
import fr.epita.clickandplay.model.Session;
import fr.epita.clickandplay.model.Table;
import fr.epita.clickandplay.model.User;
import fr.epita.clickandplay.repository.IInscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class InscriptionService {

	private final IInscriptionRepository inscriptionRepository;
	private final TableService tableService;
	private final UserService userService;

	@Autowired
	public InscriptionService(
			IInscriptionRepository inscriptionRepository,
			TableService tableService,
			UserService userService
	) {
		this.inscriptionRepository = inscriptionRepository;
		this.tableService = tableService;
		this.userService = userService;
	}

	public void register(Long tableId) {
		Table table = tableService.getTableEntity(tableId);

		if (table.getSession().getStartTime().isBefore(LocalDateTime.now()))
			throw new ForbiddenException("The Session has expired");

		String username = getCurrentUsername();
		User user = userService.getUserEntity(username);

		if (!user.isContributor() &&
				!user.isFirstSession() &&
				tableRegisteredCountInSession(user, table.getSession().getId()) == 0
		)
			throw new ForbiddenException("Seuls les cotisants peuvent s’inscrire (hors première séance).");

		boolean alreadyInTable = table.getInscriptions().stream()
				.anyMatch(i -> i.getUser().getUsername().equals(username));
		if (alreadyInTable) throw new ForbiddenException("Déjà inscrit à cette table.");

		List<Inscription> inscriptions = inscriptionRepository.findByUserUsername(username);

		LocalDateTime start = table.getStartTime();
		LocalDateTime end = start.plusMinutes(table.getDuration());

		boolean overlap = inscriptions.stream().anyMatch(insc -> {
			Table other = insc.getTable();
			LocalDateTime oStart = other.getStartTime();
			LocalDateTime oEnd = oStart.plusMinutes(other.getDuration());
			return start.isBefore(oEnd) && oStart.isBefore(end);
		});
		if (overlap) throw new ConflictException("Conflit avec une autre table.");

		if (table.getInscriptions().size() >= table.getMaxPlayers())
			throw new ForbiddenException("Cette table est déjà pleine.");

		Inscription inscription = new Inscription();
		inscription.setUser(user);
		inscription.setTable(table);
		inscriptionRepository.save(inscription);

		if (user.isFirstSession()) {
			user.setFirstSession(false);
			userService.saveUser(user);
		}
		System.out.println("[INFO] " + username + " s’est inscrit à " + table.getGameName());
	}

	public void unregister(Long tableId) {
		String username = getCurrentUsername();
		Inscription inscription = inscriptionRepository.findByUserUsernameAndTableId(username, tableId)
				.orElseThrow(() -> new NotFoundException("Inscription introuvable."));

		if (inscription.getTable().getSession().getStartTime().isBefore(LocalDateTime.now()))
			throw new ForbiddenException("The Session has expired");

		// give back first session try if unregistered to all table of a session
		if (!inscription.getUser().isContributor() &&
				tableRegisteredCountInSession(inscription.getUser(), tableId) == 1
		) {
			inscription.getUser().setFirstSession(true);
			userService.saveUser(inscription.getUser());
		}

		inscription.getUser().getInscriptions().remove(inscription);
		inscriptionRepository.delete(inscription);
		System.out.println("[INFO] " + username + " s’est désinscrit de la table ID " + tableId);
	}

	private String getCurrentUsername() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth.getName();
	}

	private Long tableRegisteredCountInSession(User user, Long sessionId) {
		return user.getInscriptions().stream().filter(
				inscription -> Objects.equals(inscription.getTable().getSession().getId(), sessionId)
				).count();
	}
}
