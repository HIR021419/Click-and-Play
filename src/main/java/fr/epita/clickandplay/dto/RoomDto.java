package fr.epita.clickandplay.dto;

import fr.epita.clickandplay.model.Room;

public class RoomDto {
	public Long id;
	public String name;
	public int capacity;
	public double rentPrice;

	public RoomDto(Room room) {
		this.id = room.getId();
		this.name = room.getName();
		this.capacity = room.getCapacity();
		this.rentPrice = room.getRentPrice();
	}
}
