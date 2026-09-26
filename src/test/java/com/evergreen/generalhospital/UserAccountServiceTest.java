package com.evergreen.generalhospital;

import com.evergreen.generalhospital.dto.admin.AdminPractitionerRequest;
import com.evergreen.generalhospital.dto.useraccount.UserAccountRegisterRequest;
import com.evergreen.generalhospital.dto.useraccount.UserAccountRequest;
import com.evergreen.generalhospital.errors.PatientIdentityMismatchException;
import com.evergreen.generalhospital.errors.RepeatedIdNumberException;
import com.evergreen.generalhospital.errors.RepeatedUsernameException;
import com.evergreen.generalhospital.errors.UnclearUserRoleException;
import com.evergreen.generalhospital.models.patient.Patient;
import com.evergreen.generalhospital.models.practitioner.Practitioner;
import com.evergreen.generalhospital.models.useraccount.Role;
import com.evergreen.generalhospital.models.useraccount.UserAccount;
import com.evergreen.generalhospital.repositories.PatientRepository;
import com.evergreen.generalhospital.repositories.PractitionerRepository;
import com.evergreen.generalhospital.repositories.UserAccountRepository;
import com.evergreen.generalhospital.services.PatientService;
import com.evergreen.generalhospital.services.PractitionerService;
import com.evergreen.generalhospital.services.UserAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private PractitionerRepository practitionerRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientService patientService;

    @Mock
    private PractitionerService practitionerService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserAccountService userAccountService;

    private Patient patient;
    private Practitioner practitioner;

    /**
     * Builds the shared patient and practitioner fixtures used by service-level
     * auth tests.
     */
    @BeforeEach
    void setUp() {
        patient = new Patient(
                "John",
                "Doe",
                "1234567890",
                LocalDate.of(1995, 4, 18),
                "male",
                "+1 555 0100",
                "john@example.com",
                "MRN-1234567890",
                "123 Main St");

        practitioner = new Practitioner(
                "Shoko",
                "Ieiri",
                "7482736581",
                LocalDate.of(1992, 6, 12),
                "female",
                "+1 555 0200",
                "shoko@example.com");
    }

    @Test
    @Disabled("Future feature: administrative account creation will be handled in a dedicated admin branch")
    /**
     * Verifies that a patient-linked account is created when role and patient
     * reference are consistent.
     */
    void adminCreateUserAccountCreatesPatientAccountWhenRoleMatchesPatientLink() {
        UUID patientId = UUID.randomUUID();
        UserAccountRequest request = new UserAccountRequest(
                "John Doe",
                "john.doe",
                null,
                patientId,
                "keycloak",
                "subject-1",
                Role.ROLE_PATIENT,
                "john.doe@example.com",
                "strong-password");

        when(userAccountRepository.existsByUsername(request.username())).thenReturn(false);
        when(userAccountRepository.existsByEmail(request.email())).thenReturn(false);
        when(userAccountRepository.existsByProviderAndProviderSubject(request.provider(), request.providerSubject()))
                .thenReturn(false);
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");
        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserAccount created = userAccountService.adminCreateUserAccount(request);

        assertThat(created.getPerson()).isSameAs(patient);
        assertThat(created.getRoles()).contains(Role.ROLE_PATIENT);
    }

    @Test
    @Disabled("Future feature: administrative account creation will be handled in a dedicated admin branch")
    /**
     * Verifies that administrative accounts can be created without domain links.
     */
    void adminCreateUserAccountCreatesAdminAccountWithoutDomainLink() {
        UserAccountRequest request = new UserAccountRequest(
                "Front Desk Admin",
                "frontdesk.admin",
                null,
                null,
                "keycloak",
                "subject-2",
                Role.ROLE_ADMIN,
                "frontdesk.admin@example.com",
                "strong-password");

        when(userAccountRepository.existsByUsername(request.username())).thenReturn(false);
        when(userAccountRepository.existsByEmail(request.email())).thenReturn(false);
        when(userAccountRepository.existsByProviderAndProviderSubject(request.provider(), request.providerSubject()))
                .thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");
        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserAccount created = userAccountService.adminCreateUserAccount(request);

        assertThat(created.getPerson()).isNull();
        assertThat(created.getRoles()).contains(Role.ROLE_ADMIN);
    }

    @Test
    @Disabled("Future feature: administrative account creation will be handled in a dedicated admin branch")
    /**
     * Verifies that practitioner accounts are rejected when no practitioner link is
     * provided.
     */
    void adminCreateUserAccountRejectsPractitionerRoleWithoutPractitionerLink() {
        UserAccountRequest request = new UserAccountRequest(
                "Shoko Ieiri",
                "shoko.ieiri",
                null,
                null,
                "keycloak",
                "subject-3",
                Role.ROLE_PRACTITIONER,
                "shoko.ieiri@example.com",
                "strong-password");

        when(userAccountRepository.existsByUsername(request.username())).thenReturn(false);
        when(userAccountRepository.existsByEmail(request.email())).thenReturn(false);
        when(userAccountRepository.existsByProviderAndProviderSubject(request.provider(), request.providerSubject()))
                .thenReturn(false);

        assertThatThrownBy(() -> userAccountService.adminCreateUserAccount(request))
                .isInstanceOf(UnclearUserRoleException.class)
                .hasMessage("Role does not match the linked patient/practitioner reference");

        verify(userAccountRepository, never()).save(any());
    }

    @Test
    @Disabled("Future feature: administrative account creation will be handled in a dedicated admin branch")
    /**
     * Verifies that creation fails when the referenced patient id does not exist.
     */
    void adminCreateUserAccountRejectsMissingPatientReference() {
        UUID patientId = UUID.randomUUID();
        UserAccountRequest request = new UserAccountRequest(
                "John Doe",
                "john.doe",
                null,
                patientId,
                "keycloak",
                "subject-4",
                Role.ROLE_PATIENT,
                "john.doe@example.com",
                "strong-password");

        when(userAccountRepository.existsByUsername(request.username())).thenReturn(false);
        when(userAccountRepository.existsByEmail(request.email())).thenReturn(false);
        when(userAccountRepository.existsByProviderAndProviderSubject(request.provider(), request.providerSubject()))
                .thenReturn(false);
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userAccountService.adminCreateUserAccount(request))
                .isInstanceOf(UnclearUserRoleException.class)
                .hasMessage("Patient link does not exist");
    }

    @Test
    @Disabled("Future feature: administrative account creation will be handled in a dedicated admin branch")
    /**
     * Verifies that username uniqueness is enforced before any domain lookups
     * occur.
     */
    void adminCreateUserAccountRejectsDuplicateUsername() {
        UserAccountRequest request = new UserAccountRequest(
                "John Doe",
                "john.doe",
                null,
                null,
                "keycloak",
                "subject-5",
                Role.ROLE_ADMIN,
                "john.doe@example.com",
                "strong-password");

        when(userAccountRepository.existsByUsername(request.username())).thenReturn(true);

        assertThatThrownBy(() -> userAccountService.adminCreateUserAccount(request))
                .isInstanceOf(RepeatedUsernameException.class)
                .hasMessage("User with username john.doe already exists");

        verify(userAccountRepository, never()).save(any());
        verify(patientRepository, never()).findById(any());
        verify(practitionerRepository, never()).findById(any());
    }

    private UserAccountRegisterRequest registrationRequest() {
        return new UserAccountRegisterRequest(
                "John",
                "Doe",
                "1234567890",
                LocalDate.of(1995, 4, 18),
                "male",
                "+1 555 0100",
                "john@example.com",
                "123 Main St",
                "john.doe@example.com",
                "strong-password");
    }

    @Test
    /**
     * Verifies that self-service registration creates a patient account linked to
     * the resolved patient with the patient role and email-based username.
     */
    void registerUserAccountCreatesPatientAccountLinkedToResolvedPatient() {
        UserAccountRegisterRequest request = registrationRequest();

        when(userAccountRepository.existsByEmail(request.email())).thenReturn(false);
        when(userAccountRepository.existsByUsername(request.email())).thenReturn(false);
        when(patientService.resolveOrCreateByIdNumber(
                eq(request.idNumber()), anyString(), anyString(), eq(request.dateOfBirth()),
                anyString(), anyString(), anyString(), anyString())).thenReturn(patient);
        when(userAccountRepository.existsByPerson(patient)).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");
        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserAccount created = userAccountService.registerPatientAccount(request);

        assertThat(created.getRoles()).contains(Role.ROLE_PATIENT);
        assertThat(created.getPerson()).isSameAs(patient);
        assertThat(created.getUsername()).isEqualTo(request.email());
        assertThat(created.getDisplayName()).isEqualTo("John Doe");
        assertThat(created.getProvider()).isEqualTo("local");
    }

    @Test
    /**
     * Verifies that registration rejects a duplicate email before touching the
     * patient.
     */
    void registerUserAccountRejectsDuplicateEmail() {
        UserAccountRegisterRequest request = registrationRequest();

        when(userAccountRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> userAccountService.registerPatientAccount(request))
                .isInstanceOf(RepeatedUsernameException.class)
                .hasMessage("User with email john.doe@example.com already exists");

        verify(userAccountRepository, never()).save(any());
        verify(patientService, never()).resolveOrCreateByIdNumber(any(), any(), any(), any(), any(), any(), any(),
                any());
    }

    @Test
    /**
     * Verifies that an idNumber that already owns an account cannot be registered
     * again.
     */
    void registerUserAccountRejectsIdNumberAlreadyLinkedToAccount() {
        UserAccountRegisterRequest request = registrationRequest();

        when(userAccountRepository.existsByEmail(request.email())).thenReturn(false);
        when(userAccountRepository.existsByUsername(request.email())).thenReturn(false);
        when(patientService.resolveOrCreateByIdNumber(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(patient);
        when(userAccountRepository.existsByPerson(patient)).thenReturn(true);

        assertThatThrownBy(() -> userAccountService.registerPatientAccount(request))
                .isInstanceOf(RepeatedUsernameException.class)
                .hasMessage("A user account already exists for idNumber 1234567890");

        verify(userAccountRepository, never()).save(any());
    }

    @Test
    /**
     * Verifies that an identity mismatch is propagated and no account is created.
     */
    void registerUserAccountRejectsIdentityMismatch() {
        UserAccountRegisterRequest request = registrationRequest();

        when(userAccountRepository.existsByEmail(request.email())).thenReturn(false);
        when(userAccountRepository.existsByUsername(request.email())).thenReturn(false);
        when(patientService.resolveOrCreateByIdNumber(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new PatientIdentityMismatchException(
                        "Patient identity for idNumber 1234567890 does not match the provided information"));

        assertThatThrownBy(() -> userAccountService.registerPatientAccount(request))
                .isInstanceOf(PatientIdentityMismatchException.class);

        verify(userAccountRepository, never()).save(any());
    }

    private AdminPractitionerRequest adminPractitionerRequest() {
        return new AdminPractitionerRequest(
                "Sarah",
                "Connor",
                "1000000001",
                LocalDate.of(1980, 5, 15),
                "female",
                "+1 555 0101",
                "John Connor (+1 555 0191)",
                List.of(UUID.randomUUID()),
                List.of("Cardiology"),
                "sarah.connor@example.com",
                null,
                "password123");
    }

    @Test
    void createPractitionerAccountCreatesPractitionerAccountWithRoleAndPersonLink() {
        AdminPractitionerRequest request = adminPractitionerRequest();

        when(practitionerRepository.existsByIdNumber(request.idNumber())).thenReturn(false);
        when(userAccountRepository.existsByEmail(request.email())).thenReturn(false);
        when(userAccountRepository.existsByUsername(request.email())).thenReturn(false);
        when(practitionerService.createPractitioner(request)).thenReturn(practitioner);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");
        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserAccount created = userAccountService.createPractitionerAccount(request);

        assertThat(created.getRoles()).contains(Role.ROLE_PRACTITIONER);
        assertThat(created.getPerson()).isSameAs(practitioner);
        assertThat(created.getUsername()).isEqualTo(request.email());
        assertThat(created.getEmail()).isEqualTo(request.email());
        assertThat(created.getDisplayName()).isEqualTo("Sarah Connor");
        assertThat(created.getProvider()).isEqualTo("local");
    }

    @Test
    void createPractitionerAccountWithExplicitUsernameUsesProvidedUsername() {
        AdminPractitionerRequest request = new AdminPractitionerRequest(
                "Sarah",
                "Connor",
                "1000000001",
                LocalDate.of(1980, 5, 15),
                "female",
                "+1 555 0101",
                "John Connor (+1 555 0191)",
                List.of(UUID.randomUUID()),
                List.of("Cardiology"),
                "sarah.connor@example.com",
                "dr.connor",
                "password123");

        when(practitionerRepository.existsByIdNumber(request.idNumber())).thenReturn(false);
        when(userAccountRepository.existsByEmail(request.email())).thenReturn(false);
        when(userAccountRepository.existsByUsername("dr.connor")).thenReturn(false);
        when(practitionerService.createPractitioner(request)).thenReturn(practitioner);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");
        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserAccount created = userAccountService.createPractitionerAccount(request);

        assertThat(created.getUsername()).isEqualTo("dr.connor");
        assertThat(created.getEmail()).isEqualTo("sarah.connor@example.com");
    }

    @Test
    void createPractitionerAccountRejectsDuplicateIdNumber() {
        AdminPractitionerRequest request = adminPractitionerRequest();

        when(practitionerRepository.existsByIdNumber(request.idNumber())).thenReturn(true);

        assertThatThrownBy(() -> userAccountService.createPractitionerAccount(request))
                .isInstanceOf(RepeatedIdNumberException.class)
                .hasMessage("Practitioner with idNumber 1000000001 already exists");

        verify(userAccountRepository, never()).save(any());
        verify(practitionerService, never()).createPractitioner(any(AdminPractitionerRequest.class));
    }

    @Test
    void createPractitionerAccountRejectsDuplicateEmail() {
        AdminPractitionerRequest request = adminPractitionerRequest();

        when(practitionerRepository.existsByIdNumber(request.idNumber())).thenReturn(false);
        when(userAccountRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> userAccountService.createPractitionerAccount(request))
                .isInstanceOf(RepeatedUsernameException.class)
                .hasMessage("User with email sarah.connor@example.com already exists");

        verify(userAccountRepository, never()).save(any());
        verify(practitionerService, never()).createPractitioner(any(AdminPractitionerRequest.class));
    }

    @Test
    void createPractitionerAccountRejectsDuplicateUsername() {
        AdminPractitionerRequest request = new AdminPractitionerRequest(
                "Sarah",
                "Connor",
                "1000000001",
                LocalDate.of(1980, 5, 15),
                "female",
                "+1 555 0101",
                "John Connor (+1 555 0191)",
                List.of(UUID.randomUUID()),
                List.of("Cardiology"),
                "sarah.connor@example.com",
                "dr.connor",
                "password123");

        when(practitionerRepository.existsByIdNumber(request.idNumber())).thenReturn(false);
        when(userAccountRepository.existsByEmail(request.email())).thenReturn(false);
        when(userAccountRepository.existsByUsername("dr.connor")).thenReturn(true);

        assertThatThrownBy(() -> userAccountService.createPractitionerAccount(request))
                .isInstanceOf(RepeatedUsernameException.class)
                .hasMessage("User with username dr.connor already exists");

        verify(userAccountRepository, never()).save(any());
        verify(practitionerService, never()).createPractitioner(any(AdminPractitionerRequest.class));
    }
}
