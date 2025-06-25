package fr.epita.clickandplay.repository;

import fr.epita.clickandplay.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<User, String> {}