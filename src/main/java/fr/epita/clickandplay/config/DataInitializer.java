package fr.epita.clickandplay.config;

import fr.epita.clickandplay.model.*;
import fr.epita.clickandplay.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

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
            AppUser admin = new AppUser("admin", "{noop}admin", Role.ADMIN, true, false);
            AppUser animator = new AppUser("animateur", "{noop}anim", Role.ANIMATOR, true, false);
            AppUser meeple = new AppUser("meeple", "{noop}meeple", Role.PLAYER, false, true);
            AppUser goldenMeeple = new AppUser("goldenmeeple", "{noop}gold", Role.PLAYER, true, false);

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
            session1.setDuration(3); // en heures
            session1.setRoom(room1);
            sessionRepo.save(session1);

            Session session2 = new Session();
            session2.setName("Vendredi Donjons & Dragons");
            session2.setStartTime(LocalDateTime.now().plusDays(4).withHour(20).withMinute(0));
            session2.setDuration(4);
            session2.setRoom(room2);
            sessionRepo.save(session2);

            // TABLES
            GameBoard gameBoard1 = new GameBoard();
            gameBoard1.setGameName("Loup-Garou");
            gameBoard1.setStartTime(session1.getStartTime());
            gameBoard1.setDuration(90); // minutes
            gameBoard1.setMaxPlayers(6);
            gameBoard1.setSession(session1);
            tableRepo.save(gameBoard1);

            GameBoard gameBoard2 = new GameBoard();
            gameBoard2.setGameName("Table Libre");
            gameBoard2.setStartTime(session1.getStartTime().plusMinutes(90));
            gameBoard2.setDuration(90);
            gameBoard2.setMaxPlayers(room1.getCapacity()); // ne compte pas dans capacité
            gameBoard2.setSession(session1);
            tableRepo.save(gameBoard2);

            GameBoard gameBoard3 = new GameBoard();
            gameBoard3.setGameName("Donjons & Dragons");
            gameBoard3.setStartTime(session2.getStartTime());
            gameBoard3.setDuration(120);
            gameBoard3.setMaxPlayers(5);
            gameBoard3.setSession(session2);
            tableRepo.save(gameBoard3);

            // INSCRIPTION
            Inscription insc1 = new Inscription();
            insc1.setAppUser(goldenMeeple);
            insc1.setGameBoard(gameBoard1);
            inscriptionRepo.save(insc1);

            Inscription insc2 = new Inscription();
            insc2.setAppUser(meeple);
            insc2.setGameBoard(gameBoard2);
            inscriptionRepo.save(insc2);
        };
    }
}
