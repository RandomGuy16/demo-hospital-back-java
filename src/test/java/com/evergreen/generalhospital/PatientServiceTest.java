package com.evergreen.generalhospital;

import com.evergreen.generalhospital.errors.PatientIdentityMismatchException;
import com.evergreen.generalhospital.models.patient.Patient;
import com.evergreen.generalhospital.repositories.PatientRepository;
import com.evergreen.generalhospital.services.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private com.evergreen.generalhospital.repositories.UserAccountRepository userAccountRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient shellPatient;

    @BeforeEach
    void setUp() {
        shellPatient = new Patient(
                null,
                null,
                "1234567890",
                null,
                null,
                "+1 555 0100",
                "guest@example.com",
                "MRN-1234567890",
                "fake street");
    }

    @Test
    void findOrCreateGuestPatientCreatesShellWhenIdNumberIsNew() {
        when(patientRepository.findByIdNumber("1234567890")).thenReturn(Optional.empty());
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Patient created = patientService.findOrCreateGuestPatient(
                "1234567890", "+1 555 0100", "guest@example.com", "fake street");

        assertThat(created.getIdNumber()).isEqualTo("1234567890");
        assertThat(created.getFirstName()).isNull();
        assertThat(created.getLastName()).isNull();
        assertThat(created.getDateOfBirth()).isNull();
        assertThat(created.getMrn()).isNotBlank();
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void findOrCreateGuestPatientReusesExistingPatient() {
        when(patientRepository.findByIdNumber("1234567890")).thenReturn(Optional.of(shellPatient));

        Patient resolved = patientService.findOrCreateGuestPatient(
                "1234567890", "+1 555 0100", "guest@example.com", "fake street");

        assertThat(resolved).isSameAs(shellPatient);
        verify(patientRepository, never()).save(any());
    }

    @Test
    void resolveOrCreateByIdNumberCreatesPatientWhenIdNumberIsNew() {
        when(patientRepository.findByIdNumber("1234567890")).thenReturn(Optional.empty());
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Patient created = patientService.resolveOrCreateByIdNumber(
                "1234567890",
                "John",
                "Doe",
                LocalDate.of(1995, 4, 18),
                "male",
                "+1 555 0100",
                "john@example.com",
                "123 Main St");

        assertThat(created.getFirstName()).isEqualTo("John");
        assertThat(created.getLastName()).isEqualTo("Doe");
        assertThat(created.getDateOfBirth()).isEqualTo(LocalDate.of(1995, 4, 18));
        assertThat(created.getMrn()).isNotBlank();
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void resolveOrCreateByIdNumberFillsGuestShellPatient() {
        when(patientRepository.findByIdNumber("1234567890")).thenReturn(Optional.of(shellPatient));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Patient resolved = patientService.resolveOrCreateByIdNumber(
                "1234567890",
                "John",
                "Doe",
                LocalDate.of(1995, 4, 18),
                "male",
                "+1 555 0100",
                "john@example.com",
                "123 Main St");

        assertThat(resolved).isSameAs(shellPatient);
        assertThat(resolved.getFirstName()).isEqualTo("John");
        assertThat(resolved.getLastName()).isEqualTo("Doe");
        assertThat(resolved.getDateOfBirth()).isEqualTo(LocalDate.of(1995, 4, 18));
        verify(patientRepository).save(shellPatient);
    }

    @Test
    void resolveOrCreateByIdNumberRejectsIdentityMismatch() {
        Patient existing = new Patient(
                "John",
                "Doe",
                "1234567890",
                LocalDate.of(1995, 4, 18),
                "male",
                "+1 555 0100",
                "john@example.com",
                "MRN-1234567890",
                "123 Main St");
        when(patientRepository.findByIdNumber("1234567890")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> patientService.resolveOrCreateByIdNumber(
                "1234567890",
                "Jane",
                "Roe",
                LocalDate.of(1995, 4, 18),
                "female",
                "+1 555 0100",
                "jane@example.com",
                "456 Other St"))
                .isInstanceOf(PatientIdentityMismatchException.class)
                .hasMessageContaining("does not match");

        verify(patientRepository, never()).save(any());
    }

    @Test
    void resolveOrCreateByIdNumberAcceptsMatchingIdentity() {
        Patient existing = new Patient(
                "John",
                "Doe",
                "1234567890",
                LocalDate.of(1995, 4, 18),
                "male",
                "+1 555 0100",
                "john@example.com",
                "MRN-1234567890",
                "123 Main St");
        when(patientRepository.findByIdNumber("1234567890")).thenReturn(Optional.of(existing));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Patient resolved = patientService.resolveOrCreateByIdNumber(
                "1234567890",
                "john",
                "doe",
                LocalDate.of(1995, 4, 18),
                "male",
                "+1 555 0200",
                "john2@example.com",
                "789 New St");

        assertThat(resolved).isSameAs(existing);
        assertThat(resolved.getPhoneNumber()).isEqualTo("+1 555 0200");
        verify(patientRepository).save(existing);
    }
}