package com.example.demo;

import com.example.demo.dto.AppointmentRequest;
import com.example.demo.errors.AppointmentCollisionException;
import com.example.demo.errors.ResourceNotFoundException;
import com.example.demo.errors.SelfDiagnosisConflictException;
import com.example.demo.models.appointment.Appointment;
import com.example.demo.models.appointment.AppointmentStatus;
import com.example.demo.models.department.Department;
import com.example.demo.models.patient.Patient;
import com.example.demo.models.practitioner.Practitioner;
import com.example.demo.repositories.AppointmentRepository;
import com.example.demo.repositories.DepartmentRepository;
import com.example.demo.repositories.PatientRepository;
import com.example.demo.repositories.PractitionerRepository;
import com.example.demo.services.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PractitionerRepository practitionerRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Patient patient;
    private Practitioner practitioner;
    private Department department;
    private AppointmentRequest request;

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

        department = new Department("Cardiology", "Handles heart care");

        request = new AppointmentRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusMinutes(45),
                AppointmentStatus.SCHEDULED
        );
    }

    @Test
    void createAppointmentSavesWhenRequestIsValid() {
        when(patientRepository.findById(request.patientId())).thenReturn(Optional.of(patient));
        when(practitionerRepository.findById(request.practitionerId())).thenReturn(Optional.of(practitioner));
        when(departmentRepository.findById(request.departmentId())).thenReturn(Optional.of(department));
        when(appointmentRepository.existsByPatient_PatientIdAndStartBeforeAndEndAfter(
                eq(request.patientId()), eq(request.end()), eq(request.start()))).thenReturn(false);
        when(appointmentRepository.existsByPractitioner_PractitionerIdAndStartBeforeAndEndAfter(
                eq(request.practitionerId()), eq(request.end()), eq(request.start()))).thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Appointment created = appointmentService.createAppointment(request);

        ArgumentCaptor<Appointment> appointmentCaptor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentRepository).save(appointmentCaptor.capture());
        Appointment saved = appointmentCaptor.getValue();

        assertThat(created).isSameAs(saved);
        assertThat(saved.getPatient()).isSameAs(patient);
        assertThat(saved.getPractitioner()).isSameAs(practitioner);
        assertThat(saved.getDepartment()).isSameAs(department);
        assertThat(saved.getStart()).isEqualTo(request.start());
        assertThat(saved.getEnd()).isEqualTo(request.end());
        assertThat(saved.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
    }

    @Test
    void createAppointmentThrowsWhenPatientDoesNotExist() {
        when(patientRepository.findById(request.patientId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.createAppointment(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Patient not found");

        verify(practitionerRepository, never()).findById(any());
        verify(departmentRepository, never()).findById(any());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void createAppointmentThrowsWhenPractitionerMatchesPatientIdentity() {
        practitioner.setIdNumber(patient.getIdNumber());

        when(patientRepository.findById(request.patientId())).thenReturn(Optional.of(patient));
        when(practitionerRepository.findById(request.practitionerId())).thenReturn(Optional.of(practitioner));
        when(departmentRepository.findById(request.departmentId())).thenReturn(Optional.of(department));

        assertThatThrownBy(() -> appointmentService.createAppointment(request))
                .isInstanceOf(SelfDiagnosisConflictException.class)
                .hasMessage("Patient and practitioner cannot be the same");

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void createAppointmentThrowsWhenPatientHasTimeCollision() {
        when(patientRepository.findById(request.patientId())).thenReturn(Optional.of(patient));
        when(practitionerRepository.findById(request.practitionerId())).thenReturn(Optional.of(practitioner));
        when(departmentRepository.findById(request.departmentId())).thenReturn(Optional.of(department));
        when(appointmentRepository.existsByPatient_PatientIdAndStartBeforeAndEndAfter(
                eq(request.patientId()), eq(request.end()), eq(request.start()))).thenReturn(true);

        assertThatThrownBy(() -> appointmentService.createAppointment(request))
                .isInstanceOf(AppointmentCollisionException.class)
                .hasMessage("Appointment time collision detected");

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void updateAppointmentReturnsUpdatedEntityWhenAppointmentExists() {
        Appointment existing = new Appointment(
                patient,
                practitioner,
                department,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusMinutes(30),
                AppointmentStatus.SCHEDULED
        );

        Patient updatedPatient = new Patient(
                "Anita",
                "Bath",
                "9000000001",
                LocalDate.of(1988, 2, 29),
                "female",
                "+1 555 0300",
                "anita@example.com",
                "MRN-9000000001",
                "456 Side St"
        );

        Practitioner updatedPractitioner = new Practitioner(
                "Holly",
                "Day",
                "9000000002",
                LocalDate.of(1990, 10, 31),
                "nonbinary",
                "+1 555 0400",
                "holly@example.com"
        );

        Department updatedDepartment = new Department("Neurology", "Handles nervous system care");

        AppointmentRequest updateRequest = new AppointmentRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(10).plusMinutes(30),
                AppointmentStatus.COMPLETED
        );

        UUID appointmentId = UUID.randomUUID();

        when(patientRepository.findById(updateRequest.patientId())).thenReturn(Optional.of(updatedPatient));
        when(practitionerRepository.findById(updateRequest.practitionerId())).thenReturn(Optional.of(updatedPractitioner));
        when(departmentRepository.findById(updateRequest.departmentId())).thenReturn(Optional.of(updatedDepartment));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existing));
        when(appointmentRepository.existsByPatient_PatientIdAndAppointmentIdNotAndStartBeforeAndEndAfter(
                eq(updateRequest.patientId()), eq(appointmentId), eq(updateRequest.end()), eq(updateRequest.start()))).thenReturn(false);
        when(appointmentRepository.existsByPractitioner_PractitionerIdAndAppointmentIdNotAndStartBeforeAndEndAfter(
                eq(updateRequest.practitionerId()), eq(appointmentId), eq(updateRequest.end()), eq(updateRequest.start()))).thenReturn(false);
        when(appointmentRepository.save(existing)).thenReturn(existing);

        Optional<Appointment> updated = appointmentService.updateAppointment(appointmentId, updateRequest);

        assertThat(updated).contains(existing);
        assertThat(existing.getPatient()).isSameAs(updatedPatient);
        assertThat(existing.getPractitioner()).isSameAs(updatedPractitioner);
        assertThat(existing.getDepartment()).isSameAs(updatedDepartment);
        assertThat(existing.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
    }

    @Test
    void updateAppointmentIgnoresCurrentAppointmentWhenCheckingCollisions() {
        UUID appointmentId = UUID.randomUUID();
        Appointment existing = new Appointment(
                patient,
                practitioner,
                department,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusMinutes(30),
                AppointmentStatus.SCHEDULED
        );

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existing));
        when(patientRepository.findById(request.patientId())).thenReturn(Optional.of(patient));
        when(practitionerRepository.findById(request.practitionerId())).thenReturn(Optional.of(practitioner));
        when(departmentRepository.findById(request.departmentId())).thenReturn(Optional.of(department));
        when(appointmentRepository.existsByPatient_PatientIdAndAppointmentIdNotAndStartBeforeAndEndAfter(
                eq(request.patientId()), eq(appointmentId), eq(request.end()), eq(request.start()))).thenReturn(false);
        when(appointmentRepository.existsByPractitioner_PractitionerIdAndAppointmentIdNotAndStartBeforeAndEndAfter(
                eq(request.practitionerId()), eq(appointmentId), eq(request.end()), eq(request.start()))).thenReturn(false);
        when(appointmentRepository.save(existing)).thenReturn(existing);

        Optional<Appointment> updated = appointmentService.updateAppointment(appointmentId, request);

        assertThat(updated).contains(existing);
        verify(appointmentRepository).existsByPatient_PatientIdAndAppointmentIdNotAndStartBeforeAndEndAfter(
                request.patientId(), appointmentId, request.end(), request.start());
        verify(appointmentRepository).existsByPractitioner_PractitionerIdAndAppointmentIdNotAndStartBeforeAndEndAfter(
                request.practitionerId(), appointmentId, request.end(), request.start());
    }

    @Test
    void getAllAppointmentsDelegatesToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Appointment> page = new PageImpl<>(List.of());
        when(appointmentRepository.findAll(pageable)).thenReturn(page);

        Page<Appointment> result = appointmentService.getAllAppointments(pageable);

        assertThat(result).isSameAs(page);
    }

    @Test
    void deleteAppointmentDeletesWhenFound() {
        UUID appointmentId = UUID.randomUUID();
        Appointment appointment = new Appointment(
                patient,
                practitioner,
                department,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusMinutes(30),
                AppointmentStatus.SCHEDULED
        );
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        Optional<Appointment> deleted = appointmentService.deleteAppointment(appointmentId);

        assertThat(deleted).contains(appointment);
        verify(appointmentRepository).delete(appointment);
    }
}
