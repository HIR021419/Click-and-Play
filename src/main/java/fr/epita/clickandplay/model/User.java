package fr.epita.clickandplay.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
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

    public User(String username, String password, Role role, boolean contributor, boolean firstSession) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.contributor = contributor;
        this.firstSession = firstSession;
    }
}
