package com.shoptestlab.auth;

import com.shoptestlab.user.User;
import com.shoptestlab.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository users;

    @Mock
    JwtService jwt;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private AuthService service() {
        return new AuthService(users, encoder, jwt);
    }

    @Test
    void login_returnsToken() {
        User user = new User(
                "user@test.local",
                encoder.encode("password123"),
                User.Role.USER
        );

        when(users.findByEmail("user@test.local")).thenReturn(Optional.of(user));
        when(jwt.generate(anyString(), anyString())).thenReturn("token");

        assertEquals(
                "token",
                service().login(
                        new AuthDtos.LoginRequest("user@test.local", "password123")
                ).token()
        );
    }

    @Test
    void login_rejectsWrongPassword() {
        User user = new User(
                "user@test.local",
                encoder.encode("password123"),
                User.Role.USER
        );

        when(users.findByEmail("user@test.local")).thenReturn(Optional.of(user));

        assertThrows(
                BadCredentialsException.class,
                () -> service().login(
                        new AuthDtos.LoginRequest("user@test.local", "wrong-password")
                )
        );

        verifyNoInteractions(jwt);
    }

    @Test
    void register_createsUserWithHashedPasswordAndUserRole() {
        when(users.existsByEmail("new@test.local")).thenReturn(false);
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwt.generate(anyString(), anyString())).thenReturn("token");

        AuthDtos.AuthResponse response = service().register(
                new AuthDtos.RegisterRequest("new@test.local", "password123")
        );

        assertEquals("token", response.token());

        verify(users).save(argThat(user ->
                user.getEmail().equals("new@test.local")
                        && user.getRole() == User.Role.USER
                        && !user.getPassword().equals("password123")
                        && encoder.matches("password123", user.getPassword())
        ));
    }

    @Test
    void register_rejectsDuplicateEmail() {
        when(users.existsByEmail("existing@test.local")).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> service().register(
                        new AuthDtos.RegisterRequest("existing@test.local", "password123")
                )
        );

        verify(users, never()).save(any());
        verifyNoInteractions(jwt);
    }
}
