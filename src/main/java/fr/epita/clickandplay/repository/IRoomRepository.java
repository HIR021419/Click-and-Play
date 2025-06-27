package fr.epita.clickandplay.repository;

import fr.epita.clickandplay.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRoomRepository extends JpaRepository<Room, Long> {}
