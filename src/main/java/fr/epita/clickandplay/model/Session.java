package fr.epita.clickandplay.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
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
    private Set<GameBoard> gameBoards = new HashSet<>();

    public Session(String name, LocalDateTime startTime, int duration, Room room) {
        this.name = name;
        this.startTime = startTime;
        this.duration = duration;
        this.room = room;
    }
}
