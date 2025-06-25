package fr.epita.clickandplay.session;

import fr.epita.clickandplay.room.Room;
import fr.epita.clickandplay.table.Table;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDateTime startTime;

    private int duration;

    @ManyToOne
    private Room room;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Table> tables = new HashSet<>();
}
