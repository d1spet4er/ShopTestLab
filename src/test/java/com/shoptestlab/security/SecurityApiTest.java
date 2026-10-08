package com.shoptestlab.security;

import com.shoptestlab.auth.JwtService;
import com.shoptestlab.user.User;
import com.shoptestlab.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:shoptestlab;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.jwt.secret=test-secret-test-secret-test-secret-test-secret-123456",
        "app.jwt.expiration-ms=3600000"
})
class SecurityApiTest {

    @Autowired MockMvc mvc;
    @Autowired UserRepository users;
    @Autowired PasswordEncoder encoder;
    @Autowired JwtService jwt;

    @Test
    void protectedEndpointWithoutTokenReturns401() throws Exception {
        mvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerValidatesEmailAndPassword() throws Exception {
        mvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"bad-email","password":"123"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.email").exists())
                .andExpect(jsonPath("$.fields.password").exists());
    }

    @Test
    void registeredUserCanAccessProfileWithJwt() throws Exception {
        User user = users.save(new User(
                "security@test.local",
                encoder.encode("password123"),
                User.Role.USER
        ));
        String token = jwt.generate(user.getEmail(), user.getRole().name());

        mvc.perform(get("/api/v1/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("security@test.local"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void invalidJwtReturns401() throws Exception {
        mvc.perform(get("/api/v1/users/me")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Test
    void userCannotCreateProduct() throws Exception {
        User user = users.save(new User(
                "regular@test.local",
                encoder.encode("password123"),
                User.Role.USER
        ));
        String token = jwt.generate(user.getEmail(), user.getRole().name());

        mvc.perform(post("/api/v1/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Test product","description":"test","price":10.00,"stock":5,"category":"test"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void userCannotDeleteProduct() throws Exception {
        User user = users.save(new User(
                "delete@test.local",
                encoder.encode("password123"),
                User.Role.USER
        ));
        String token = jwt.generate(user.getEmail(), user.getRole().name());

        mvc.perform(delete("/api/v1/products/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void publicProductListDoesNotRequireAuthentication() throws Exception {
        mvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk());
    }
}
