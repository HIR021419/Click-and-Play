package fr.epita.clickandplay.dto;

import java.time.LocalDateTime;

public class CreateSessionDto {
	public String name;
	public LocalDateTime startTime;
	public int duration;
	public Long roomId;
}
