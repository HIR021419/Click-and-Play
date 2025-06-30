package fr.epita.clickandplay.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
public class CreateTableDto {
	public String gameName;
	public int maxPlayers;
	public LocalDateTime startTime;
	public int duration;
	public Long sessionId;
}
