package fr.epita.clickandplay.dto;

import fr.epita.clickandplay.model.Session;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
public class SessionDto {
	public Long id;
	public String name;
	public LocalDateTime startTime;
	public int duration;
	public Long roomId;

	public SessionDto(Session session) {
		this.id = session.getId();
		this.name = session.getName();
		this.startTime = session.getStartTime();
		this.duration = session.getDuration();
		this.roomId = session.getRoom().getId();
	}
}
