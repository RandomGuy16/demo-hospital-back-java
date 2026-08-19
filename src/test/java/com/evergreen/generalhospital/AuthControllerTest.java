package com.evergreen.generalhospital;

import com.evergreen.generalhospital.dto.useraccount.UserAccountLoginRequest;
import com.evergreen.generalhospital.dto.useraccount.UserAccountRegisterRequest;
import com.evergreen.generalhospital.testsupport.base.AuthControllerTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "security.jwt.secret=${JWT_SECRET:MDEyMzQ1Njc4OWFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVowMTIzNDU2Nzg5}",
    "security.jwt.expiration-ms=86400000",
    "security.jwt.issuer=${JWT_ISSUER:evergreen-general-hospital-api}"
})
public class AuthControllerTest extends AuthControllerTestSupport {

    @Test
    /**
     * Verifies that local registration with demographics returns a signed JWT
     * for a new patient account.
     */
    public void testRegisterReturnsCreated() throws Exception {
        UserAccountRegisterRequest request = new UserAccountRegisterRequest(
            "Gregory",
            "House",
            "5550000001",
            LocalDate.of(1995, 4, 18),
            "male",
            "+1 555 0199",
            "greg.house@example.com",
            "123 Main St",
            "gregory.house@example.com",
            "123456"
        );

        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    /**
     * Verifies that registration links the new account to an existing patient
     * when the demographics and idNumber match a previously booked guest.
     */
    public void testRegisterLinksExistingPatient() throws Exception {
        UserAccountRegisterRequest request = new UserAccountRegisterRequest(
            defaultSubjects.patient().getFirstName(),
            defaultSubjects.patient().getLastName(),
            defaultSubjects.patient().getIdNumber(),
            defaultSubjects.patient().getDateOfBirth(),
            defaultSubjects.patient().getGender(),
            "+1 555 0100",
            "john.doe@example.com",
            "123 Main St",
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
    /**
     * Verifies that registration rejects an idNumber whose stored demographics
     * do not match the payload (anti-hijacking).
     */
    public void testRegisterWithMismatchedIdentityReturnsConflict() throws Exception {
        UserAccountRegisterRequest request = new UserAccountRegisterRequest(
            "Jane",
            "Roe",
            defaultSubjects.patient().getIdNumber(),
            LocalDate.of(2000, 1, 1),
            "female",
            "+1 555 0100",
            "jane.roe@example.com",
            "123 Main St",
            "jane.roe@gmail.com",
            "123456"
        );

        mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
            .andExpect(status().isConflict());
    }

    @Test
    /**
     * Verifies that an idNumber can only own a single account.
     */
    public void testRegisterWithIdNumberAlreadyRegisteredReturnsConflict() throws Exception {
        UserAccountRegisterRequest request = new UserAccountRegisterRequest(
            "Gregory",
            "House",
            "5550000002",
            LocalDate.of(1995, 4, 18),
            "male",
            "+1 555 0199",
            "greg.house@example.com",
            "123 Main St",
            "greg.house@gmail.com",
            "123456"
        );

        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
            .andExpect(status().isConflict());
    }

    @Test
    /**
     * Verifies that a seeded local user can exchange valid credentials for a JWT.
     */
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
    /**
     * Verifies that bad credentials are translated into a 401 API error response.
     */
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
    /**
     * Verifies that login fails with 401 when no local account exists for the email.
     */
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
    /**
     * Verifies that `/api/v1/me` reads identity and authorities from the authenticated JWT.
     */
    void testMeReturnsOk() throws Exception {
        cleanDatabase();
        TestUserSubjects guineaPigs = seedUserSubjects();

        // Use spring-security-test to inject a JWT-backed Authentication into the secured MockMvc request.
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
    /**
     * Verifies that `/api/v1/me` rejects anonymous requests.
     */
    void testMeWithoutJwtReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/me"))
            .andExpect(status().isUnauthorized());
    }
}
