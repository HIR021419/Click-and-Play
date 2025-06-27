package fr.epita.clickandplay.repository;

import fr.epita.clickandplay.model.Table;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITableRepository extends JpaRepository<Table, Long> {}
