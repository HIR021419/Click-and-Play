package fr.epita.clickandplay.repository;

import fr.epita.clickandplay.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IInscriptionRepository extends JpaRepository<Inscription, Long> {
    List<Inscription> findByUserUsername(String username);
    Optional<Inscription> findByUserUsernameAndTableId(String username, Long tableId);
}
