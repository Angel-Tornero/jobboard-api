package com.jobboard.backend.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobboard.backend.dto.LoginRequest;
import com.jobboard.backend.dto.SignupRequest;
import com.jobboard.backend.model.User;
import com.jobboard.backend.repository.UserRepository;
import com.jobboard.backend.security.JwtUtil;


@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String signup(SignupRequest request) {
        logger.info("Signup attempt for email: {}", request.email);
        if (userRepository.existsByEmail(request.email)) {
            logger.warn("Signup failed: Email already in use - {}", request.email);
            throw new RuntimeException("Email is already in use.");
        }

        User user = new User();
        user.setEmail(request.email);
        user.setPassword(passwordEncoder.encode(request.password));
        user.setRole("employer");

        userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        logger.info("User registered successfully: {}", request.email);
        return token;
    }

    public String login(LoginRequest request) {
        logger.info("Login attempt for email: {}", request.email);
        User user = userRepository.findByEmail(request.email)
            .orElseThrow(() -> {
                logger.warn("Login failed: User not found - {}", request.email);
                return new RuntimeException("User not found");
            });

        if (!passwordEncoder.matches(request.password, user.getPassword())) {
            logger.warn("Login failed: Incorrect password for email - {}", request.email);
            throw new RuntimeException("Incorrect password");
        }

        String token = jwtUtil.generateToken(user);
        logger.info("User logged in successfully: {}", request.email);
        return token;
    }
}
