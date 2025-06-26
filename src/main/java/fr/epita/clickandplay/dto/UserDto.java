package fr.epita.clickandplay.dto;

import fr.epita.clickandplay.model.User;

public class UserDto {
    public String username;
    public String role;
    public boolean contributor;
    public boolean firstSession;

    public UserDto(User user) {
        this.username = user.getUsername();
        this.role = user.getRole().toString();
        this.contributor = user.isContributor();
        this.firstSession = user.isFirstSession();
    }
}
