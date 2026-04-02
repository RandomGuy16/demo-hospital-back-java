package com.example.demo;

import com.example.demo.dto.UserAccountLoginRequest;
import com.example.demo.dto.UserAccountRegisterRequest;
import com.example.demo.models.useraccount.Role;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "security.jwt.secret=${JWT_SECRET}",
    "security.jwt.expiration-ms=86400000",
    "security.jwt.issuer=${JWT_ISSUER:demo-api}"
})
public class AuthControllerTest extends AuthControllerTestSupport {

    @Test
    public void testRegisterReturnsCreated() throws Exception {
        UserAccountRegisterRequest request = new UserAccountRegisterRequest(
            "Gregory House",
            "g.house",
            null,
            null,
            Role.ROLE_ADMIN,
            "example@gmail.com",
            "123456"
        );

        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    public void testRegisterWithExistingPatient() throws Exception {
        UserAccountRegisterRequest request = new UserAccountRegisterRequest(
            "John Doe",
            "j.doe",
            null,
            defaultSubjects.patient().getPatientId(),
            Role.ROLE_PATIENT,
            "jdoe@gmail.com",
            "123456"
        );

        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    public void testRegisterWithNonExistingPractitionerReturnsBadRequest() throws Exception {
        UserAccountRegisterRequest request = new UserAccountRegisterRequest(
            "Shoko Ieiri",
            "shk.iei",
            UUID.randomUUID(),
            null,
            Role.ROLE_PRACTITIONER,
            "anotherexample@gmail.com",
            "123456789"
        );

        mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginReturnsOk() throws Exception {
        cleanDatabase();
        seedUserSubjects();

        UserAccountLoginRequest request = new UserAccountLoginRequest(
            "admin@example.com",
            "admin-password"
        );

        mockMvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void testLoginWithInvalidCredentialsReturnsUnauthorized() throws Exception {
        cleanDatabase();
        seedUserSubjects();
        UserAccountLoginRequest request = new UserAccountLoginRequest(
            "admin@example.com",
            "invalid"
        );

        mockMvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void testLoginWithNonExistingUserReturnsUnauthorized() throws Exception {
        UserAccountLoginRequest request = new UserAccountLoginRequest(
            "idk@idk.com",
            "invalid"
        );

        mockMvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void testMeReturnsOk() throws Exception {
        cleanDatabase();
        TestUserSubjects guineaPigs = seedUserSubjects();

        mockMvc.perform(get("/api/v1/me")
                .with(jwt().jwt(jwt -> jwt
                        .subject(guineaPigs.admin().getEmail())
                        .claim("email", guineaPigs.admin().getEmail())
                        .claim("name", guineaPigs.admin().getDisplayName())
                        .claim("preferred_username", guineaPigs.admin().getUsername())
                        .claim("roles", List.of(guineaPigs.admin().getRole().name()))
                ).authorities(new SimpleGrantedAuthority(guineaPigs.admin().getRole().name()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.subject").value(guineaPigs.admin().getEmail()))
            .andExpect(jsonPath("$.email").value(guineaPigs.admin().getEmail()))
            .andExpect(jsonPath("$.name").value(guineaPigs.admin().getDisplayName()))
            .andExpect(jsonPath("$.preferredUsername").value(guineaPigs.admin().getUsername()))
            .andExpect(jsonPath("$.authorities[0]").value(guineaPigs.admin().getRole().name()));
    }

    @Test
    void testMeWithoutJwtReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/me"))
            .andExpect(status().isUnauthorized());
    }
}
