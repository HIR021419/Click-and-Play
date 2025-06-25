package fr.epita.clickandplay.user;

import fr.epita.clickandplay.inscription.Inscription;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User {
    @Id
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean contributor;

    private boolean firstSession = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Inscription> inscriptions = new HashSet<>();
}
