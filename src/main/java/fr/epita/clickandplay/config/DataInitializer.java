package fr.epita.clickandplay.config;

import fr.epita.clickandplay.model.*;
import fr.epita.clickandplay.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Bean
	public CommandLineRunner initData(
			IUserRepository userRepo,
			IRoomRepository roomRepo,
			ISessionRepository sessionRepo,
			ITableRepository tableRepo,
			IInscriptionRepository inscriptionRepo
	) {
		return args -> {

			// USERS
			User admin = new User("admin", passwordEncoder.encode("admin"), Role.ADMIN, true, false);
			User animator = new User("animateur", passwordEncoder.encode("anim"), Role.ANIMATOR, true, false);
			User meeple = new User("meeple", passwordEncoder.encode("meeple"), Role.PLAYER, false, true);
			User goldenMeeple = new User("goldenmeeple", passwordEncoder.encode("gold"), Role.PLAYER, true, false);

			userRepo.save(admin);
			userRepo.save(animator);
			userRepo.save(meeple);
			userRepo.save(goldenMeeple);

			// ROOMS
			Room room1 = new Room("Salle 1", 10, 20.0);
			roomRepo.save(room1);

			Room room2 = new Room("Salle 2", 8, 15.0);
			roomRepo.save(room2);

			// SESSIONS
			Session session1 = new Session();
			session1.setName("Mercredi des Loups-Garous");
			session1.setStartTime(LocalDateTime.now().plusDays(2).withHour(18).withMinute(0));
			session1.setDuration(3);
			session1.setRoom(room1);
			sessionRepo.save(session1);

			Session session2 = new Session();
			session2.setName("Vendredi Donjons & Dragons");
			session2.setStartTime(LocalDateTime.now().plusDays(4).withHour(20).withMinute(0));
			session2.setDuration(4);
			session2.setRoom(room2);
			sessionRepo.save(session2);

			// TABLES
			Table table1 = new Table();
			table1.setGameName("Loup-Garou");
			table1.setStartTime(session1.getStartTime());
			table1.setDuration(90);
			table1.setMaxPlayers(6);
			table1.setSession(session1);
			tableRepo.save(table1);

			Table table2 = new Table();
			table2.setGameName("Table Libre");
			table2.setStartTime(session1.getStartTime().plusMinutes(90));
			table2.setDuration(90);
			table2.setMaxPlayers(room1.getCapacity());
			table2.setSession(session1);
			tableRepo.save(table2);

			Table table3 = new Table();
			table3.setGameName("Donjons & Dragons");
			table3.setStartTime(session2.getStartTime());
			table3.setDuration(120);
			table3.setMaxPlayers(5);
			table3.setSession(session2);
			tableRepo.save(table3);

			// INSCRIPTION
			Inscription insc1 = new Inscription();
			insc1.setUser(goldenMeeple);
			insc1.setTable(table1);
			inscriptionRepo.save(insc1);

			Inscription insc2 = new Inscription();
			insc2.setUser(meeple);
			insc2.setTable(table2);
			inscriptionRepo.save(insc2);
		};
	}
}
