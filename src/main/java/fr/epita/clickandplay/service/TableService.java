package fr.epita.clickandplay.service;

import fr.epita.clickandplay.dto.TableDto;
import fr.epita.clickandplay.exception.ConflictException;
import fr.epita.clickandplay.exception.NotFoundException;
import fr.epita.clickandplay.model.Session;
import fr.epita.clickandplay.model.Table;
import fr.epita.clickandplay.repository.ISessionRepository;
import fr.epita.clickandplay.repository.ITableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TableService {

    private final ITableRepository tableRepository;
    private final ISessionRepository sessionRepository;

    @Autowired
    public TableService(ITableRepository tableRepository, ISessionRepository sessionRepository) {
        this.tableRepository = tableRepository;
        this.sessionRepository = sessionRepository;
    }

    public TableDto createTable(Long sessionId, TableDto dto) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Séance introuvable"));

        LocalDateTime sessionStart = session.getStartTime();
        LocalDateTime sessionEnd = sessionStart.plusHours(session.getDuration());

        LocalDateTime tableStart = dto.startTime;
        LocalDateTime tableEnd = tableStart.plusMinutes(dto.duration);

        if (tableStart.isBefore(sessionStart) || tableEnd.isAfter(sessionEnd.minusMinutes(15))) {
            throw new ConflictException("La partie doit commencer et finir dans la plage horaire de la séance, et se terminer au moins 15 min avant la fin.");
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
                System.out.println("[MAIL] À " + insc.getUser().getUsername() + " : la partie '" + table.getGameName() + "' a été annulée.")
        );

        tableRepository.delete(table);
    }

    public List<TableDto> getTablesForSession(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Séance introuvable"));

        return session.getTables().stream()
                .map(TableDto::new)
                .collect(Collectors.toList());
    }
}
