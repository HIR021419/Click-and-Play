package fr.epita.clickandplay.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Room {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private int capacity;
	private double rentPrice;

	public Room(String name, int capacity, double rentPrice) {
		this.name = name;
		this.capacity = capacity;
		this.rentPrice = rentPrice;
	}
}
