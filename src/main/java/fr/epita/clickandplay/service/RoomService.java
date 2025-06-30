package fr.epita.clickandplay.service;

import fr.epita.clickandplay.dto.CreateRoomDto;
import fr.epita.clickandplay.dto.RoomDto;
import fr.epita.clickandplay.exception.NotFoundException;
import fr.epita.clickandplay.model.Room;
import fr.epita.clickandplay.repository.IRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

	private final IRoomRepository roomRepository;

	@Autowired
	public RoomService(IRoomRepository roomRepository) {
		this.roomRepository = roomRepository;
	}

	public Room getRoomById(Long id) {
		return roomRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Salle non trouvée"));
	}

	public List<RoomDto> getAllRooms() {
		return roomRepository.findAll().stream()
				.map(RoomDto::new)
				.collect(Collectors.toList());
	}

	public RoomDto createRoom(CreateRoomDto dto) {
		Room room = new Room();
		room.setName(dto.name);
		room.setCapacity(dto.capacity);
		room.setRentPrice(dto.rentPrice);

		room = roomRepository.save(room);

		return new RoomDto(room);
	}
}
