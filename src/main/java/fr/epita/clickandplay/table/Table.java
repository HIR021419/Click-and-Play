package fr.epita.clickandplay.table;

import fr.epita.clickandplay.inscription.Inscription;
import fr.epita.clickandplay.session.Session;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gameName;

    private int maxPlayers;

    private LocalDateTime startTime;

    private int duration;

    @ManyToOne
    private Session session;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL)
    private Set<Inscription> inscriptions = new HashSet<>();
}
