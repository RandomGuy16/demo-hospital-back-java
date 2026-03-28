package com.example.demo;

import com.example.demo.dto.UserAccountLoginRequest;
import com.example.demo.dto.UserAccountRegisterRequest;
import com.example.demo.models.useraccount.Role;
import com.example.demo.services.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends ControllerTestSupport{

    @InjectMocks
    private JwtService jwtService;
    
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

    @Test void testLoginReturnsOk() throws Exception {
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

    @Test void testLoginWithInvalidCredentialsReturnsUnauthorized() throws Exception {
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

    @Test void testLoginWithNonExistingUserReturnsUnauthorized() throws Exception {
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

    @Test void testMeReturnsOk() throws Exception {
        cleanDatabase();
        TestUserSubjects guineaPigs = seedUserSubjects();

        String token = jwtService.generateToken(guineaPigs.admin());

        mockMvc.perform(get("/api/v1/me")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value(guineaPigs.admin().getUsername()))
            .andExpect(jsonPath("$.displayName").value(guineaPigs.admin().getDisplayName()))
            .andExpect(jsonPath("$.email").value(guineaPigs.admin().getEmail()))
            .andExpect(jsonPath("$.role").value(guineaPigs.admin().getRole().name()));
        /*
        @Schema(example = "00u123example") String subject,
        @Schema(example = "https://accounts.google.com") String issuer,
        @Schema(example = "jane.doe@example.com") String email,
        @Schema(example = "Jane Doe") String name,
        @Schema(example = "jane.doe") String preferredUsername,
        @Schema(example = "[\"SCOPE_openid\", \"ROLE_ADMIN\"]") List<String> authorities
         */
    }

    @Test void testMeWithoutJwtReturnsUnauthorized() throws Exception {}

}
