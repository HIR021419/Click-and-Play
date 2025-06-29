package fr.epita.clickandplay.controller;

import fr.epita.clickandplay.dto.TableDto;
import fr.epita.clickandplay.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TableController {

	private final TableService tableService;

	@Autowired
	public TableController(TableService tableService) {
		this.tableService = tableService;
	}

	/**
	 * Liste les tables pour une session donnée
	 */
	@GetMapping("/sessions/{sessionId}/tables")
	public ResponseEntity<List<TableDto>> getTablesForSession(@PathVariable Long sessionId) {
		return ResponseEntity.ok(tableService.getTablesForSession(sessionId));
	}

	/**
	 * Crée une table dans une session (réservé aux animateurs ou admin)
	 */
	@PostMapping("/tables")
	@PreAuthorize("hasAnyRole('ADMIN', 'ANIMATOR')")
	public ResponseEntity<TableDto> createTable(
			@RequestBody TableDto dto
	) {
		return new ResponseEntity<>(tableService.createTable(dto), HttpStatus.CREATED);
	}

	/**
	 * Supprime une table et notifie les inscrits
	 */
	@DeleteMapping("/tables/{tableId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'ANIMATOR')")
	public ResponseEntity<Void> deleteTable(@PathVariable Long tableId) {
		tableService.deleteTable(tableId);
		return ResponseEntity.noContent().build();
	}
}
