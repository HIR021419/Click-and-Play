package fr.epita.clickandplay.service;

import fr.epita.clickandplay.exception.ConflictException;
import fr.epita.clickandplay.exception.ForbiddenException;
import fr.epita.clickandplay.exception.NotFoundException;
import fr.epita.clickandplay.model.*;
import fr.epita.clickandplay.repository.IInscriptionRepository;
import fr.epita.clickandplay.repository.ITableRepository;
import fr.epita.clickandplay.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InscriptionService {

    private final IInscriptionRepository inscriptionRepository;
    private final ITableRepository tableRepository;
    private final IUserRepository userRepository;

    @Autowired
    public InscriptionService(IInscriptionRepository inscriptionRepository, ITableRepository tableRepository, IUserRepository userRepository) {
        this.inscriptionRepository = inscriptionRepository;
        this.tableRepository = tableRepository;
        this.userRepository = userRepository;
    }

    public void register(Long tableId) {
        Table table = tableRepository.findById(tableId)
                .orElseThrow(() -> new NotFoundException("Table introuvable"));

        String username = getCurrentUsername();
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        if (!user.isContributor() && !user.isFirstSession()) {
            throw new ForbiddenException("Seuls les cotisants peuvent s’inscrire (hors première séance).");
        }

        boolean alreadyInTable = table.getInscriptions().stream()
                .anyMatch(i -> i.getUser().getUsername().equals(username));

        if (alreadyInTable) {
            throw new ForbiddenException("Déjà inscrit à cette table.");
        }

        List<Inscription> inscriptions = inscriptionRepository.findByUserUsername(username);

        LocalDateTime start = table.getStartTime();
        LocalDateTime end = start.plusMinutes(table.getDuration());

        boolean chevauchement = inscriptions.stream().anyMatch(inscription -> {
            Table other = inscription.getTable();
            LocalDateTime oStart = other.getStartTime();
            LocalDateTime oEnd = oStart.plusMinutes(other.getDuration());
            return start.isBefore(oEnd) && oStart.isBefore(end);
        });

        if (chevauchement) {
            throw new ConflictException("Conflit avec une autre table à laquelle vous êtes déjà inscrit.");
        }

        if (table.getInscriptions().size() >= table.getMaxPlayers()) {
            throw new ForbiddenException("Cette table est déjà pleine.");
        }

        Inscription insc = new Inscription();
        insc.setUser(user);
        insc.setTable(table);
        inscriptionRepository.save(insc);

        if (user.isFirstSession()) {
            user.setFirstSession(false);
            userRepository.save(user);
        }

        System.out.println("[INFO] " + username + " s’est inscrit à la table " + table.getGameName());
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
