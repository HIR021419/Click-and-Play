package fr.epita.clickandplay.controller;

import fr.epita.clickandplay.service.InscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registrations")
public class InscriptionController {

	private final InscriptionService inscriptionService;

	@Autowired
	public InscriptionController(InscriptionService inscriptionService) {
		this.inscriptionService = inscriptionService;
	}

	/**
	 * S’inscrit à une table donnée
	 */
	@PostMapping("/{tableId}")
	public ResponseEntity<String> registerToTable(@PathVariable Long tableId) {
		inscriptionService.register(tableId);
		return ResponseEntity.ok("Inscription réussie.");
	}

	/**
	 * Se désinscrit d’une table
	 */
	@DeleteMapping("/{tableId}")
	public ResponseEntity<String> unregisterFromTable(@PathVariable Long tableId) {
		inscriptionService.unregister(tableId);
		return ResponseEntity.ok("Désinscription réussie.");
	}
}
