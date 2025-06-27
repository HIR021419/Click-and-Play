package fr.epita.clickandplay.service;

import fr.epita.clickandplay.dto.SessionDto;
import fr.epita.clickandplay.exception.BadRequestException;
import fr.epita.clickandplay.exception.ConflictException;
import fr.epita.clickandplay.exception.NotFoundException;
import fr.epita.clickandplay.model.Room;
import fr.epita.clickandplay.model.Session;
import fr.epita.clickandplay.repository.IRoomRepository;
import fr.epita.clickandplay.repository.ISessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {

	private final ISessionRepository sessionRepository;
	private final IRoomRepository roomRepository;

	@Autowired
	public SessionService(ISessionRepository sessionRepository, IRoomRepository roomRepository) {
		this.sessionRepository = sessionRepository;
		this.roomRepository = roomRepository;
	}

	public SessionDto createSession(SessionDto dto) {
		if (dto.startTime.isBefore(LocalDateTime.now())) {
			throw new BadRequestException("La date de la séance doit être dans le futur.");
		}

		if (dto.duration <= 0) {
			throw new BadRequestException("La durée doit être positive.");
		}

		Room room = roomRepository.findById(dto.roomId)
				.orElseThrow(() -> new NotFoundException("Salle non trouvée."));

		LocalDateTime newStart = dto.startTime;
		LocalDateTime newEnd = newStart.plusHours(dto.duration);

		boolean overlap = sessionRepository.findAll().stream()
				.anyMatch(existing -> {
					LocalDateTime start = existing.getStartTime();
					LocalDateTime end = start.plusHours(existing.getDuration());
					return start.isBefore(newEnd) && newStart.isBefore(end);
				});

		if (overlap) {
			throw new ConflictException("Une autre séance se chevauche avec cette plage horaire.");
		}

		Session session = new Session();
		session.setName(dto.name);
		session.setStartTime(dto.startTime);
		session.setDuration(dto.duration);
		session.setRoom(room);

		sessionRepository.save(session);
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
