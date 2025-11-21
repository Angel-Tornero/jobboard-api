package com.jobboard.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobboard.backend.dto.auth.LoginRequest;
import com.jobboard.backend.dto.auth.SignupRequest;
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
        logger.info("Signup attempt for email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Signup failed: Email already in use - {}", request.getEmail());
            throw new IllegalArgumentException("Email is already in use.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("employer"); 

        userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        logger.info("User registered successfully: {}", request.getEmail());
        return token;
    }

    public String login(LoginRequest request) {
        logger.info("Login attempt for email: {}", request.getEmail());
        
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> {
                logger.warn("Login failed: User not found - {}", request.getEmail());
                return new BadCredentialsException("Invalid email or password");
            });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("Login failed: Incorrect password for email - {}", request.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user);
        logger.info("User logged in successfully: {}", request.getEmail());
        return token;
    }
}
