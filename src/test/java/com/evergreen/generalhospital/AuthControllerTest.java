package com.evergreen.generalhospital;

import com.evergreen.generalhospital.dto.useraccount.UserAccountLoginRequest;
import com.evergreen.generalhospital.dto.useraccount.UserAccountRegisterRequest;
import com.evergreen.generalhospital.testsupport.base.AuthControllerTestSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.evergreen.generalhospital.models.useraccount.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MvcResult;

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

    @Autowired
    private ObjectMapper objectMapper;

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
     * Verifies that the same idNumber cannot be claimed by a second user, even
     * when the email and password differ (one idNumber owns exactly one account).
     */
    public void testRegisterSameIdNumberWithDifferentEmailReturnsConflict() throws Exception {
        UserAccountRegisterRequest first = new UserAccountRegisterRequest(
            "Alan",
            "Turing",
            "5550000003",
            LocalDate.of(1912, 6, 23),
            "male",
            "+1 555 0200",
            "alan.turing@example.com",
            "123 Main St",
            "alan@example.com",
            "123456"
        );

        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(first)))
            .andExpect(status().isCreated());

        UserAccountRegisterRequest second = new UserAccountRegisterRequest(
            "Alan",
            "Turing",
            "5550000003",
            LocalDate.of(1912, 6, 23),
            "male",
            "+1 555 0200",
            "alan.turing@example.com",
            "123 Main St",
            "alan.other@example.com",
            "654321"
        );

        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(second)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @Test
    /**
     * Verifies that registration rejects a payload whose idNumber is not 10 digits.
     */
    public void testRegisterWithMalformedIdNumberReturnsBadRequest() throws Exception {
        UserAccountRegisterRequest request = new UserAccountRegisterRequest(
            "Gregory",
            "House",
            "12345",
            LocalDate.of(1995, 4, 18),
            "male",
            "+1 555 0199",
            "greg.house@example.com",
            "123 Main St",
            "greg.house@example.com",
            "123456"
        );

        mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    /**
     * Verifies that registration rejects a malformed email address.
     */
    public void testRegisterWithInvalidEmailReturnsBadRequest() throws Exception {
        UserAccountRegisterRequest request = new UserAccountRegisterRequest(
            "Gregory",
            "House",
            "5550000004",
            LocalDate.of(1995, 4, 18),
            "male",
            "+1 555 0199",
            "greg.house@example.com",
            "123 Main St",
            "not-an-email",
            "123456"
        );

        mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    /**
     * Verifies that registration rejects a password shorter than the minimum length.
     */
    public void testRegisterWithShortPasswordReturnsBadRequest() throws Exception {
        UserAccountRegisterRequest request = new UserAccountRegisterRequest(
            "Gregory",
            "House",
            "5550000005",
            LocalDate.of(1995, 4, 18),
            "male",
            "+1 555 0199",
            "greg.house@example.com",
            "123 Main St",
            "greg.house@example.com",
            "123"
        );

        mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
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
     * Verifies that login rejects a malformed email address before any lookup.
     */
    void testLoginWithInvalidEmailFormatReturnsBadRequest() throws Exception {
        UserAccountLoginRequest request = new UserAccountLoginRequest(
            "not-an-email",
            "whatever"
        );

        mockMvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    /**
     * Verifies that a freshly registered user can immediately log in.
     */
    void testRegisteredUserCanLogin() throws Exception {
        cleanDatabase();
        UserAccountRegisterRequest register = new UserAccountRegisterRequest(
            "Grace",
            "Hopper",
            "5550000006",
            LocalDate.of(1906, 12, 9),
            "female",
            "+1 555 0300",
            "grace.hopper@example.com",
            "123 Main St",
            "grace@example.com",
            "compiler1"
        );

        mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(register)))
            .andExpect(status().isCreated());

        UserAccountLoginRequest login = new UserAccountLoginRequest(
            "grace@example.com",
            "compiler1"
        );

        mockMvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(login)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    /**
     * Verifies that the token issued by registration authenticates a real request
     * against the protected `/me` endpoint (end-to-end JWT round trip).
     */
    void testRegisterTokenAuthenticatesOnMeEndpoint() throws Exception {
        cleanDatabase();
        UserAccountRegisterRequest register = new UserAccountRegisterRequest(
            "Katherine",
            "Johnson",
            "5550000007",
            LocalDate.of(1918, 8, 26),
            "female",
            "+1 555 0400",
            "katherine.johnson@example.com",
            "123 Main St",
            "katherine@example.com",
            "nasa1961"
        );

        MvcResult result = mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(register)))
            .andExpect(status().isCreated())
            .andReturn();

        String token = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();

        mockMvc.perform(get("/api/v1/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.subject").value("katherine@example.com"))
            .andExpect(jsonPath("$.email").value("katherine@example.com"))
            .andExpect(jsonPath("$.name").value("Katherine Johnson"))
            .andExpect(jsonPath("$.preferredUsername").value("katherine@example.com"))
            .andExpect(jsonPath("$.authorities[0]").value("ROLE_PATIENT"));
    }

    @Test
    /**
     * Verifies that `/api/v1/me` reads identity and authorities from the authenticated JWT.
     */
    void testMeReturnsOk() throws Exception {
        cleanDatabase();
        TestUserSubjects guineaPigs = seedUserSubjects();

        List<String> adminRoles = guineaPigs.admin().getRoles().stream().map(Role::name).toList();
        GrantedAuthority[] authorities = adminRoles.stream()
            .map(SimpleGrantedAuthority::new)
            .toArray(GrantedAuthority[]::new);

        // Use spring-security-test to inject a JWT-backed Authentication into the secured MockMvc request.
        mockMvc.perform(get("/api/v1/me")
                .with(jwt().jwt(jwt -> jwt
                        .subject(guineaPigs.admin().getEmail())
                        .claim("email", guineaPigs.admin().getEmail())
                        .claim("name", guineaPigs.admin().getDisplayName())
                        .claim("preferred_username", guineaPigs.admin().getUsername())
                        .claim("roles", adminRoles)
                ).authorities(authorities)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.subject").value(guineaPigs.admin().getEmail()))
            .andExpect(jsonPath("$.email").value(guineaPigs.admin().getEmail()))
            .andExpect(jsonPath("$.name").value(guineaPigs.admin().getDisplayName()))
            .andExpect(jsonPath("$.preferredUsername").value(guineaPigs.admin().getUsername()))
            .andExpect(jsonPath("$.authorities[0]").value(adminRoles.getFirst()));
    }

    @Test
    /**
     * Verifies that `/api/v1/me` rejects anonymous requests.
     */
    void testMeWithoutJwtReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/me"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    /**
     * Verifies that `/api/v1/patients/me` returns the logged-in patient's record.
     */
    void testPatientCanFetchTheirInfoOnPatientsMeEndpoint() throws Exception {
        cleanDatabase();
        UserAccountRegisterRequest register = new UserAccountRegisterRequest(
            "Katherine",
            "Johnson",
            "5550000007",
            LocalDate.of(1918, 8, 26),
            "female",
            "+1 555 0400",
            "katherine.johnson@example.com",
            "123 Main St",
            "katherine@example.com",
            "nasa1961"
        );

        MvcResult result = mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(register)))
            .andExpect(status().isCreated())
            .andReturn();

        String token = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();

        mockMvc.perform(get("/api/v1/patients/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Katherine"))
            .andExpect(jsonPath("$.lastName").value("Johnson"));
    }

    @Test
    @Disabled("not a use case yet")
    void testNonPatientFetchTheirInfoOnPatientsMeEndpointReturnsForbidden() throws Exception {
        //wa
    }
}
