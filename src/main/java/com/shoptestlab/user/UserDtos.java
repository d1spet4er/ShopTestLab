package com.shoptestlab.user;

public final class UserDtos {

    private UserDtos() {
    }

    public record UserResponse(
            Long id,
            String email,
            String role
    ) {
        static UserResponse from(User user) {
            return new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getRole().name()
            );
        }
    }
}
