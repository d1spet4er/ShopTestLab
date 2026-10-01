package com.shoptestlab.auth;
import jakarta.validation.constraints.*;
public final class AuthDtos { private AuthDtos(){}
 public record RegisterRequest(@Email @NotBlank String email,@NotBlank @Size(min=8,max=100) String password){}
 public record LoginRequest(@Email @NotBlank String email,@NotBlank String password){}
 public record AuthResponse(String token,String email,String role){}
}
