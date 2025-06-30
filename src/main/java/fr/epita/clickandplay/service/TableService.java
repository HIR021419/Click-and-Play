package fr.epita.clickandplay.service;

import fr.epita.clickandplay.dto.CreateTableDto;
import fr.epita.clickandplay.dto.TableDto;
import fr.epita.clickandplay.exception.*;
import fr.epita.clickandplay.model.*;
import fr.epita.clickandplay.repository.ITableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TableService {

	private final ITableRepository tableRepository;
	private final SessionService   sessionService;

	@Autowired
	public TableService(ITableRepository tableRepository, SessionService sessionService) {
		this.tableRepository = tableRepository;
		this.sessionService  = sessionService;
	}

	public Table getTableEntity(Long id) {
		return tableRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Table introuvable"));
	}

	public TableDto createTable(CreateTableDto dto) {
		// On demande la séance au service, pas directement au dépôt
		Session session = sessionService.getSessionEntity(dto.sessionId);

		LocalDateTime sessionStart = session.getStartTime();
		LocalDateTime sessionEnd   = sessionStart.plusHours(session.getDuration());

		LocalDateTime tableStart = dto.startTime;
		LocalDateTime tableEnd   = tableStart.plusMinutes(dto.duration);

		if (tableStart.isBefore(sessionStart) || tableEnd.isAfter(sessionEnd.minusMinutes(15))) {
			throw new ConflictException("La partie doit commencer et finir dans la plage horaire de la séance, " +
					"et se terminer au moins 15 min avant la fin.");
		}

		int totalMaxPlayers = session.getTables().stream()
				.filter(t -> !"Libre".equalsIgnoreCase(t.getGameName()))
				.mapToInt(Table::getMaxPlayers)
				.sum();
		if (totalMaxPlayers + dto.maxPlayers > session.getRoom().getCapacity()) {
			throw new ConflictException("Capacité de la salle dépassée par l'ajout de cette table.");
		}

		Table table = new Table();
		table.setGameName(dto.gameName);
		table.setStartTime(dto.startTime);
		table.setDuration(dto.duration);
		table.setMaxPlayers(dto.maxPlayers);
		table.setSession(session);

		table = tableRepository.save(table);
		return new TableDto(table);
	}

	public void deleteTable(Long tableId) {
		Table table = tableRepository.findById(tableId)
				.orElseThrow(() -> new NotFoundException("Table introuvable"));
		table.getInscriptions().forEach(insc ->
				System.out.println("[MAIL] À " + insc.getUser().getUsername() + " : la partie '" +
						table.getGameName() + "' a été annulée.")
		);
		tableRepository.delete(table);
	}

	public List<TableDto> getTablesForSession(Long sessionId) {
		// délègue à SessionService
		return sessionService.getTablesForSession(sessionId);
	}
}
