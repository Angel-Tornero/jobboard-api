package com.jobboard.backend.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jobboard.backend.dto.auth.LoginRequest;
import com.jobboard.backend.dto.auth.SignupRequest;
import com.jobboard.backend.model.User;
import com.jobboard.backend.repository.UserRepository;
import com.jobboard.backend.security.JwtUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        String email = "test@example.com";
        String password = "password123";

        signupRequest = new SignupRequest();
        signupRequest.setEmail(email);
        signupRequest.setPassword(password);

        loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setPassword("encodedPassword");
        user.setRole("employer");
    }

    // --- SIGNUP TESTS ---

    @Test
    void testSignup_Success() {
        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("encodedPassword");
        when(jwtUtil.generateToken(any(User.class))).thenReturn("mocked-jwt-token");

        String token = authService.signup(signupRequest);

        assertNotNull(token);
        assertEquals("mocked-jwt-token", token);
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken(any(User.class));
    }

    @Test
    void testSignup_EmailAlreadyExists() {
        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.signup(signupRequest);
        });

        assertEquals("Email is already in use.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    // --- LOGIN TESTS ---

    @Test
    void testLogin_Success() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user)).thenReturn("mocked-jwt-token");

        String token = authService.login(loginRequest);

        assertEquals("mocked-jwt-token", token);
    }

    @Test
    void testLogin_UserNotFound() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    void testLogin_WrongPassword() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Invalid email or password", exception.getMessage());
    }
}
