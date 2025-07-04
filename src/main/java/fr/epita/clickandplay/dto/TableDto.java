package fr.epita.clickandplay.dto;

import fr.epita.clickandplay.model.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
public class TableDto {
	public Long id;
	public String gameName;
	public int maxPlayers;
	public LocalDateTime startTime;
	public int duration;
	public Long sessionId;

	public TableDto(Table table) {
		this.id = table.getId();
		this.gameName = table.getGameName();
		this.maxPlayers = table.getMaxPlayers();
		this.startTime = table.getStartTime();
		this.duration = table.getDuration();
		this.sessionId = table.getSession().getId();
	}
}
