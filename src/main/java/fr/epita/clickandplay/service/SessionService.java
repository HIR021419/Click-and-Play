package fr.epita.clickandplay.service;

import fr.epita.clickandplay.dto.SessionDto;
import fr.epita.clickandplay.dto.TableDto;
import fr.epita.clickandplay.exception.BadRequestException;
import fr.epita.clickandplay.exception.ConflictException;
import fr.epita.clickandplay.exception.NotFoundException;
import fr.epita.clickandplay.model.Room;
import fr.epita.clickandplay.model.Session;
import fr.epita.clickandplay.model.Table;
import fr.epita.clickandplay.repository.IRoomRepository;
import fr.epita.clickandplay.repository.ISessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {

	private final ISessionRepository sessionRepository;
	private final RoomService        roomService;   // ← nouveau
	private final TableService       tableService;  // déjà présent

	@Autowired
	public SessionService(ISessionRepository sessionRepository,
						  RoomService roomService,
						  @Lazy TableService tableService) {   // @Lazy pour casser le cycle
		this.sessionRepository = sessionRepository;
		this.roomService       = roomService;
		this.tableService      = tableService;
	}

	public Session getSessionEntity(Long id) {
		return sessionRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Séance " + id + " introuvable."));
	}

	public List<TableDto> getTablesForSession(Long sessionId) {
		return getSessionEntity(sessionId).getTables().stream()
				.map(TableDto::new)
				.collect(Collectors.toList());
	}

	public SessionDto createSession(SessionDto dto) {
		if (dto.startTime.isBefore(LocalDateTime.now()))
			throw new BadRequestException("La date de la séance doit être dans le futur.");
		if (dto.duration <= 0)
			throw new BadRequestException("La durée doit être positive.");

		// Accès à la salle via RoomService
		Room room = roomService.getRoomById(dto.roomId);

		LocalDateTime newStart = dto.startTime;
		LocalDateTime newEnd   = newStart.plusHours(dto.duration);

		boolean overlap = sessionRepository.findAll().stream().anyMatch(existing -> {
			LocalDateTime start = existing.getStartTime();
			LocalDateTime end   = start.plusHours(existing.getDuration());
			return start.isBefore(newEnd) && newStart.isBefore(end);
		});
		if (overlap) throw new ConflictException("Une autre séance se chevauche avec cette plage horaire.");

		Session session = new Session();
		session.setName(dto.name);
		session.setStartTime(dto.startTime);
		session.setDuration(dto.duration);
		session.setRoom(room);
		sessionRepository.save(session);

		// Table « Libre » créée via TableService
		tableService.createTable(new TableDto(
				0L, "Table Libre", room.getCapacity(),
				session.getStartTime(), session.getDuration(), session.getId()
		));

		dto.id = session.getId();
		return dto;
	}

	public void deleteSession(Long id) {
		Session session = sessionRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Séance " + id + " introuvable."));

		session.getTables().forEach(table -> table
				.getInscriptions().forEach(inscription -> System.out.println(
						"[MAIL] À " + inscription.getUser().getUsername() + " : La séance a été annulée.")
				)
		);

		sessionRepository.delete(session);
	}

	public List<SessionDto> getAllFutureSessions() {
		return sessionRepository.findAll().stream()
				.filter(s -> s.getStartTime().isAfter(LocalDateTime.now()))
				.map(SessionDto::new)
				.collect(Collectors.toList());
	}

	public SessionDto getSessionById(Long id) {
		return sessionRepository.findById(id)
				.map(SessionDto::new)
				.orElseThrow(() -> new NotFoundException("Séance " + id + " introuvable."));
	}
}
