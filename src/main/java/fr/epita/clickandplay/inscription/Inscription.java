package fr.epita.clickandplay.inscription;

import fr.epita.clickandplay.table.Table;
import fr.epita.clickandplay.user.User;
import jakarta.persistence.*;

@Entity
public class Inscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Table table;
}
