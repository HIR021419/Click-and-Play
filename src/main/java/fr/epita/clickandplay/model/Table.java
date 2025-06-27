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
@Entity(name = "game_board") // table is a reserved name in H2
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

	public Table(String gameName, int maxPlayers, LocalDateTime startTime, int duration, Session session) {
		this.gameName = gameName;
		this.maxPlayers = maxPlayers;
		this.startTime = startTime;
		this.duration = duration;
		this.session = session;
	}
}
