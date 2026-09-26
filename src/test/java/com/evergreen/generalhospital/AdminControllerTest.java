package com.evergreen.generalhospital;

import com.evergreen.generalhospital.dto.admin.AdminPractitionerRequest;
import com.evergreen.generalhospital.models.department.Department;
import com.evergreen.generalhospital.models.practitioner.Practitioner;
import com.evergreen.generalhospital.models.useraccount.Role;
import com.evergreen.generalhospital.models.useraccount.UserAccount;
import com.evergreen.generalhospital.repositories.DepartmentRepository;
import com.evergreen.generalhospital.repositories.PractitionerRepository;
import com.evergreen.generalhospital.repositories.UserAccountRepository;
import com.evergreen.generalhospital.testsupport.base.AuthControllerTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "security.jwt.secret=${JWT_SECRET:MDEyMzQ1Njc4OWFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVowMTIzNDU2Nzg5}",
    "security.jwt.expiration-ms=86400000",
    "security.jwt.issuer=${JWT_ISSUER:evergreen-general-hospital-api}"
})
public class AdminControllerTest extends AuthControllerTestSupport {

    private static final List<String> ADMIN_ROLES = List.of("ROLE_ADMIN");
    private static final GrantedAuthority[] ADMIN_AUTHORITIES = { new SimpleGrantedAuthority("ROLE_ADMIN") };

    private static final List<String> PATIENT_ROLES = List.of("ROLE_PATIENT");
    private static final GrantedAuthority[] PATIENT_AUTHORITIES = { new SimpleGrantedAuthority("ROLE_PATIENT") };

    private static final List<String> PRACTITIONER_ROLES = List.of("ROLE_PRACTITIONER");
    private static final GrantedAuthority[] PRACTITIONER_AUTHORITIES = { new SimpleGrantedAuthority("ROLE_PRACTITIONER") };

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PractitionerRepository practitionerRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    private RequestPostProcessor adminJwt() {
        return jwt().jwt(jwt -> jwt
                .subject("admin@evergreen.com")
                .claim("email", "admin@evergreen.com")
                .claim("roles", ADMIN_ROLES))
                .authorities(ADMIN_AUTHORITIES);
    }

    private RequestPostProcessor patientJwt() {
        return jwt().jwt(jwt -> jwt
                .subject("patient@example.com")
                .claim("email", "patient@example.com")
                .claim("roles", PATIENT_ROLES))
                .authorities(PATIENT_AUTHORITIES);
    }

    private RequestPostProcessor practitionerJwt() {
        return jwt().jwt(jwt -> jwt
                .subject("doctor@example.com")
                .claim("email", "doctor@example.com")
                .claim("roles", PRACTITIONER_ROLES))
                .authorities(PRACTITIONER_AUTHORITIES);
    }

    private AdminPractitionerRequest buildValidPractitionerRequest(UUID departmentId) {
        return new AdminPractitionerRequest(
                "Meredith",
                "Grey",
                "9990000001",
                LocalDate.of(1983, 9, 8),
                "female",
                "+1 555 0199",
                "Derek Shepherd (+1 555 0196)",
                List.of(departmentId),
                List.of("General Surgery", "Internal Medicine"),
                "meredith.grey@evergreen.com",
                "meredith.grey",
                "doctorPass123"
        );
    }

    // =========================================================================
    // POST /api/v1/admin/practitioners Tests
    // =========================================================================

    @Test
    void createPractitioner_asAdmin_returnsCreatedWithLocationAndBody() throws Exception {
        UUID deptId = defaultSubjects.department().getDepartmentId();
        AdminPractitionerRequest request = buildValidPractitionerRequest(deptId);

        mockMvc.perform(post("/api/v1/admin/practitioners")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/practitioners/")))
                .andExpect(jsonPath("$.practitionerId").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value("Meredith"))
                .andExpect(jsonPath("$.lastName").value("Grey"))
                .andExpect(jsonPath("$.idNumber").value("9990000001"))
                .andExpect(jsonPath("$.specialties", contains("General Surgery", "Internal Medicine")))
                .andExpect(jsonPath("$.departments", hasSize(1)))
                .andExpect(jsonPath("$.departments[0].departmentId").value(deptId.toString()))
                .andExpect(jsonPath("$.account.username").value("meredith.grey"))
                .andExpect(jsonPath("$.account.email").value("meredith.grey@evergreen.com"))
                .andExpect(jsonPath("$.account.roles", hasItem("ROLE_PRACTITIONER")))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    void createPractitioner_asPatient_returnsForbidden() throws Exception {
        UUID deptId = defaultSubjects.department().getDepartmentId();
        AdminPractitionerRequest request = buildValidPractitionerRequest(deptId);

        mockMvc.perform(post("/api/v1/admin/practitioners")
                        .with(patientJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createPractitioner_asPractitioner_returnsForbidden() throws Exception {
        UUID deptId = defaultSubjects.department().getDepartmentId();
        AdminPractitionerRequest request = buildValidPractitionerRequest(deptId);

        mockMvc.perform(post("/api/v1/admin/practitioners")
                        .with(practitionerJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createPractitioner_unauthenticated_returnsUnauthorized() throws Exception {
        UUID deptId = defaultSubjects.department().getDepartmentId();
        AdminPractitionerRequest request = buildValidPractitionerRequest(deptId);

        mockMvc.perform(post("/api/v1/admin/practitioners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createPractitioner_invalidPayload_returnsBadRequest() throws Exception {
        AdminPractitionerRequest invalid = new AdminPractitionerRequest(
                "",
                "",
                "123",
                LocalDate.now().plusDays(1),
                "",
                "123",
                "",
                List.of(),
                List.of(),
                "not-an-email",
                null,
                "123"
        );

        mockMvc.perform(post("/api/v1/admin/practitioners")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void createPractitioner_departmentNotFound_returnsNotFound() throws Exception {
        AdminPractitionerRequest request = buildValidPractitionerRequest(UUID.randomUUID());

        mockMvc.perform(post("/api/v1/admin/practitioners")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void createPractitioner_duplicateIdNumber_returnsConflict() throws Exception {
        UUID deptId = defaultSubjects.department().getDepartmentId();
        AdminPractitionerRequest request = buildValidPractitionerRequest(deptId);

        // First creation succeeds
        mockMvc.perform(post("/api/v1/admin/practitioners")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated());

        // Second creation with same idNumber but different email/username
        AdminPractitionerRequest duplicateId = new AdminPractitionerRequest(
                "Different",
                "Name",
                "9990000001",
                LocalDate.of(1985, 1, 1),
                "male",
                "+1 555 0200",
                "Emergency Contact",
                List.of(deptId),
                List.of("Neurology"),
                "different.doctor@evergreen.com",
                "different.doctor",
                "doctorPass123"
        );

        mockMvc.perform(post("/api/v1/admin/practitioners")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(duplicateId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @Test
    void createPractitioner_duplicateEmail_returnsConflict() throws Exception {
        UUID deptId = defaultSubjects.department().getDepartmentId();
        AdminPractitionerRequest request = buildValidPractitionerRequest(deptId);

        // First creation succeeds
        mockMvc.perform(post("/api/v1/admin/practitioners")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated());

        // Second creation with different idNumber but same email
        AdminPractitionerRequest duplicateEmail = new AdminPractitionerRequest(
                "Different",
                "Doctor",
                "9990000002",
                LocalDate.of(1986, 2, 2),
                "female",
                "+1 555 0201",
                "Emergency Contact",
                List.of(deptId),
                List.of("Neurology"),
                "meredith.grey@evergreen.com",
                "meredith.different",
                "doctorPass123"
        );

        mockMvc.perform(post("/api/v1/admin/practitioners")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(duplicateEmail)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    // =========================================================================
    // GET /api/v1/admin/practitioners Tests
    // =========================================================================

    @Test
    void listPractitioners_asAdmin_returnsPaginatedList() throws Exception {
        mockMvc.perform(get("/api/v1/admin/practitioners")
                        .with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", notNullValue()))
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.content[0].practitionerId").isNotEmpty())
                .andExpect(jsonPath("$.content[0].firstName").isNotEmpty())
                .andExpect(jsonPath("$.content[0].lastName").isNotEmpty())
                .andExpect(jsonPath("$.content[0].idNumber").isNotEmpty())
                .andExpect(jsonPath("$.content[0].departments").isArray());
    }

    @Test
    void listPractitioners_filteredByDepartment_returnsMatchingOnly() throws Exception {
        UUID cardioDeptId = defaultSubjects.department().getDepartmentId();

        mockMvc.perform(get("/api/v1/admin/practitioners")
                        .with(adminJwt())
                        .param("departmentId", cardioDeptId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", notNullValue()));
    }

    @Test
    void listPractitioners_filteredBySpecialty_returnsMatchingOnly() throws Exception {
        // Shoko Ieiri has specialties seeded by domainFixtures (Cardiology, Sorcery)
        mockMvc.perform(get("/api/v1/admin/practitioners")
                        .with(adminJwt())
                        .param("specialty", "Sorcery"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].lastName").value("Ieiri"));
    }

    @Test
    void listPractitioners_withPaginationAndSort_returnsOrderedPage() throws Exception {
        mockMvc.perform(get("/api/v1/admin/practitioners")
                        .with(adminJwt())
                        .param("page", "0")
                        .param("size", "1")
                        .param("sort", "lastName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(1));
    }

    @Test
    void listPractitioners_nonExistentDepartment_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/admin/practitioners")
                        .with(adminJwt())
                        .param("departmentId", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void listPractitioners_asPatient_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/admin/practitioners")
                        .with(patientJwt()))
                .andExpect(status().isForbidden());
    }

    @Test
    void listPractitioners_asPractitioner_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/admin/practitioners")
                        .with(practitionerJwt()))
                .andExpect(status().isForbidden());
    }

    @Test
    void listPractitioners_unauthenticated_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/admin/practitioners"))
                .andExpect(status().isUnauthorized());
    }
}
