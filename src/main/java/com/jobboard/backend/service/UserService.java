package com.jobboard.backend.service;

import org.springframework.stereotype.Service;

import com.jobboard.backend.model.User;
import com.jobboard.backend.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
   
    public User createUser(User user) {
        validateUser(user);
        return userRepository.save(user);
    }

    public void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email must not be empty");
        }

        if (!user.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password must not be empty");
        }

        if (user.getRole() == null || (!user.getRole().equals("employee") && !user.getRole().equals("employer"))) {
            throw new IllegalArgumentException("Role must be 'employee' or 'employer'");
        }
    }
}
