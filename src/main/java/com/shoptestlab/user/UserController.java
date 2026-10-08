package com.shoptestlab.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository users;

    @GetMapping("/me")
    public UserDtos.UserResponse me(Authentication authentication) {
        return users.findByEmail(authentication.getName())
                .map(UserDtos.UserResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
