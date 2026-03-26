package com.example.demo;

import com.example.demo.dto.UserAccountRequest;
import com.example.demo.errors.RepeatedUsernameException;
import com.example.demo.errors.UnclearUserRoleException;
import com.example.demo.models.patient.Patient;
import com.example.demo.models.practitioner.Practitioner;
import com.example.demo.models.useraccount.Role;
import com.example.demo.models.useraccount.UserAccount;
import com.example.demo.repositories.PatientRepository;
import com.example.demo.repositories.PractitionerRepository;
import com.example.demo.repositories.UserAccountRepository;
import com.example.demo.services.UserAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

    @InjectMocks
    private UserAccountService userAccountService;

    private Patient patient;
    private Practitioner practitioner;

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
                "123 Main St"
        );

        practitioner = new Practitioner(
                "Shoko",
                "Ieiri",
                "7482736581",
                LocalDate.of(1992, 6, 12),
                "female",
                "+1 555 0200",
                "shoko@example.com"
        );
    }

    @Test
    void createUserAccountCreatesPatientAccountWhenRoleMatchesPatientLink() {
        UUID patientId = UUID.randomUUID();
        UserAccountRequest request = new UserAccountRequest(
                "John Doe",
                "john.doe",
                null,
                patientId,
                "keycloak",
                "subject-1",
                Role.ROLE_PATIENT,
                "john.doe@example.com"
        );

        when(userAccountRepository.existsByUsername(request.username())).thenReturn(false);
        when(userAccountRepository.existsByEmail(request.email())).thenReturn(false);
        when(userAccountRepository.existsByProviderAndProviderSubject(request.provider(), request.providerSubject())).thenReturn(false);
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserAccount created = userAccountService.createUserAccount(request);

        assertThat(created.getPatient()).isSameAs(patient);
        assertThat(created.getPractitioner()).isNull();
        assertThat(created.getRole()).isEqualTo(Role.ROLE_PATIENT);
    }

    @Test
    void createUserAccountCreatesAdminAccountWithoutDomainLink() {
        UserAccountRequest request = new UserAccountRequest(
                "Front Desk Admin",
                "frontdesk.admin",
                null,
                null,
                "keycloak",
                "subject-2",
                Role.ROLE_ADMIN,
                "frontdesk.admin@example.com"
        );

        when(userAccountRepository.existsByUsername(request.username())).thenReturn(false);
        when(userAccountRepository.existsByEmail(request.email())).thenReturn(false);
        when(userAccountRepository.existsByProviderAndProviderSubject(request.provider(), request.providerSubject())).thenReturn(false);
        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserAccount created = userAccountService.createUserAccount(request);

        assertThat(created.getPatient()).isNull();
        assertThat(created.getPractitioner()).isNull();
        assertThat(created.getRole()).isEqualTo(Role.ROLE_ADMIN);
    }

    @Test
    void createUserAccountRejectsPractitionerRoleWithoutPractitionerLink() {
        UserAccountRequest request = new UserAccountRequest(
                "Shoko Ieiri",
                "shoko.ieiri",
                null,
                null,
                "keycloak",
                "subject-3",
                Role.ROLE_PRACTITIONER,
                "shoko.ieiri@example.com"
        );

        when(userAccountRepository.existsByUsername(request.username())).thenReturn(false);
        when(userAccountRepository.existsByEmail(request.email())).thenReturn(false);
        when(userAccountRepository.existsByProviderAndProviderSubject(request.provider(), request.providerSubject())).thenReturn(false);

        assertThatThrownBy(() -> userAccountService.createUserAccount(request))
                .isInstanceOf(UnclearUserRoleException.class)
                .hasMessage("Role does not match the linked patient/practitioner reference");

        verify(userAccountRepository, never()).save(any());
    }

    @Test
    void createUserAccountRejectsMissingPatientReference() {
        UUID patientId = UUID.randomUUID();
        UserAccountRequest request = new UserAccountRequest(
                "John Doe",
                "john.doe",
                null,
                patientId,
                "keycloak",
                "subject-4",
                Role.ROLE_PATIENT,
                "john.doe@example.com"
        );

        when(userAccountRepository.existsByUsername(request.username())).thenReturn(false);
        when(userAccountRepository.existsByEmail(request.email())).thenReturn(false);
        when(userAccountRepository.existsByProviderAndProviderSubject(request.provider(), request.providerSubject())).thenReturn(false);
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userAccountService.createUserAccount(request))
                .isInstanceOf(UnclearUserRoleException.class)
                .hasMessage("Patient link does not exist");
    }

    @Test
    void createUserAccountRejectsDuplicateUsername() {
        UserAccountRequest request = new UserAccountRequest(
                "John Doe",
                "john.doe",
                null,
                null,
                "keycloak",
                "subject-5",
                Role.ROLE_ADMIN,
                "john.doe@example.com"
        );

        when(userAccountRepository.existsByUsername(request.username())).thenReturn(true);

        assertThatThrownBy(() -> userAccountService.createUserAccount(request))
                .isInstanceOf(RepeatedUsernameException.class)
                .hasMessage("User with username john.doe already exists");

        verify(userAccountRepository, never()).save(any());
        verify(patientRepository, never()).findById(any());
        verify(practitionerRepository, never()).findById(any());
    }
}
