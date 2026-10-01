package com.shoptestlab.auth;

import com.shoptestlab.user.User;
import com.shoptestlab.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock UserRepository users;
    @Mock JwtService jwt;
    @InjectMocks AuthService service;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void login_returnsToken() {
        User user = new User("user@test.local", encoder.encode("password123"), User.Role.USER);
        when(users.findByEmail("user@test.local")).thenReturn(Optional.of(user));
        when(jwt.generate(anyString(), anyString())).thenReturn("token");

        AuthService actual = new AuthService(users, encoder, jwt);
        assertEquals("token", actual.login(new AuthDtos.LoginRequest("user@test.local", "password123")).token());
    }
}
