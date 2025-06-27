package fr.epita.clickandplay.repository;

import fr.epita.clickandplay.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISessionRepository extends JpaRepository<Session, Long> {}
