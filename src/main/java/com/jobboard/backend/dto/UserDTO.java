package com.jobboard.backend.dto;

import com.jobboard.backend.model.User;

public class UserDTO {
    private Long id;
    private String email;
    private String role;

    public UserDTO() {}

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.role = user.getRole();
    }
}
