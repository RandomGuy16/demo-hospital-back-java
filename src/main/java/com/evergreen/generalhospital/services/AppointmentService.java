package com.evergreen.generalhospital.services;

import com.evergreen.generalhospital.dto.appointment.AppointmentRequest;
import com.evergreen.generalhospital.dto.appointment.GuestAppointmentRequest;
import com.evergreen.generalhospital.errors.AppointmentCollisionException;
import com.evergreen.generalhospital.errors.ResourceNotFoundException;
import com.evergreen.generalhospital.errors.SelfDiagnosisConflictException;
import com.evergreen.generalhospital.models.appointment.Appointment;
import com.evergreen.generalhospital.models.department.Department;
import com.evergreen.generalhospital.models.patient.Patient;
import com.evergreen.generalhospital.models.practitioner.Practitioner;
import com.evergreen.generalhospital.repositories.AppointmentRepository;
import com.evergreen.generalhospital.repositories.DepartmentRepository;
import com.evergreen.generalhospital.repositories.PatientRepository;
import com.evergreen.generalhospital.repositories.PractitionerRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class AppointmentService {
    // repositories direct links
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final PractitionerRepository practitionerRepository;
    private final DepartmentRepository departmentRepository;
    private final PatientService patientService;

    // simple record to temporarily store the input after validating
    private record AppointmentRefs (
        Patient patient,
        Practitioner practitioner,
        Department department
    ) { }


    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              PractitionerRepository practitionerRepository,
                              DepartmentRepository departmentRepository,
                              PatientService patientService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.practitionerRepository = practitionerRepository;
        this.departmentRepository = departmentRepository;
        this.patientService = patientService;
    }

    // method for validating the no collision of an appointment
    private void validateAppointmentNoCollision(UUID patientId, UUID practitionerId,
                                                LocalDateTime start, LocalDateTime end) throws AppointmentCollisionException {
        boolean doesPatientHaveCollision = appointmentRepository
            .existsByPatient_IdAndStartBeforeAndEndAfter(patientId, end, start);

        boolean doesPractitionerHaveCollision = appointmentRepository
            .existsByPractitioner_IdAndStartBeforeAndEndAfter(practitionerId, end, start);

        if (doesPatientHaveCollision || doesPractitionerHaveCollision)
            throw new AppointmentCollisionException("Appointment time collision detected");
    }

    // same as before but for update requests
    private void validateAppointmentNoCollision(UUID appointmentId, UUID patientId, UUID practitionerId,
                                                LocalDateTime start, LocalDateTime end) throws AppointmentCollisionException {
        boolean doesPatientHaveCollision = appointmentRepository
            .existsByPatient_IdAndAppointmentIdNotAndStartBeforeAndEndAfter(
                patientId,
                appointmentId,
                end,
                start
            );

        boolean doesPractitionerHaveCollision = appointmentRepository
            .existsByPractitioner_IdAndAppointmentIdNotAndStartBeforeAndEndAfter(
                practitionerId,
                appointmentId,
                end,
                start
            );

        if (doesPatientHaveCollision || doesPractitionerHaveCollision) {
            throw new AppointmentCollisionException("Appointment time collision detected");
        }
    }

    private AppointmentRefs validateAppointmentParticipantsExistence(UUID patientId, UUID practitionerId, UUID departmentId) throws ResourceNotFoundException {
        // findById().orElseThrow() is one query but loads the whole model
        // existsById() and getReferenceById() is lightweight but 2 roundtrips to database

        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Practitioner practitioner = practitionerRepository.findById(practitionerId)
            .orElseThrow(() -> new ResourceNotFoundException("Practitioner not found"));

        Department department =  departmentRepository.findById(departmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        // validate patient and practitioner ain't the same
        if (Objects.equals(patient.getIdNumber(), practitioner.getIdNumber())) {
            throw new SelfDiagnosisConflictException("Patient and practitioner cannot be the same");
        }

        return new AppointmentRefs(
            patient,
            practitioner,
            department
        );
    }

    public Appointment createAppointment(AppointmentRequest request) {
        // validate that all the participants in the appointment exist
        AppointmentRefs payload = validateAppointmentParticipantsExistence(request.patientId(), request.practitionerId(), request.departmentId());
        validateAppointmentNoCollision(request.patientId(), request.practitionerId(), request.start(), request.end());

        Appointment appointment = new Appointment(
                payload.patient,
                payload.practitioner,
                payload.department,
                request.start(),
                request.end(),
                request.status());
        return appointmentRepository.save(appointment);
    }

    /**
     * Books an appointment for an unregistered person identified by national idNumber.
     *
     * <p>The patient shell is resolved or created from the guest's idNumber and
     * contact details, then the same existence, collision, and self-diagnosis
     * validations as a normal booking are applied.</p>
     *
     * @param request guest booking payload.
     * @return the booked appointment.
     */
    public Appointment createGuestAppointment(GuestAppointmentRequest request) {
        Patient patient = patientService.findOrCreateGuestPatient(
                request.idNumber(),
                request.phoneNumber(),
                request.email(),
                request.location());

        AppointmentRefs payload = validateAppointmentParticipantsExistence(
                patient.getPatientId(),
                request.practitionerId(),
                request.departmentId());
        validateAppointmentNoCollision(patient.getPatientId(), request.practitionerId(), request.start(), request.end());

        Appointment appointment = new Appointment(
                patient,
                payload.practitioner,
                payload.department,
                request.start(),
                request.end(),
                request.status());
        return appointmentRepository.save(appointment);
    }

    public Page<Appointment> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable);
    }

    public Optional<Appointment> getAppointmentById(UUID id) {
        return appointmentRepository.findById(id);
    }

    public Optional<Appointment> updateAppointment(UUID id, AppointmentRequest request) {
        return appointmentRepository.findById(id)
                .map(appointment -> {
                    AppointmentRefs payload = validateAppointmentParticipantsExistence(request.patientId(), request.practitionerId(), request.departmentId());
                    validateAppointmentNoCollision(id, request.patientId(), request.practitionerId(), request.start(), request.end());

                    appointment.setPatient(payload.patient);
                    appointment.setPractitioner(payload.practitioner);
                    appointment.setDepartment(payload.department);
                    appointment.setStart(request.start());
                    appointment.setEnd(request.end());
                    appointment.setStatus(request.status());
                    return appointmentRepository.save(appointment);
                });
    }

    public Optional<Appointment> deleteAppointment(UUID id) {
        return appointmentRepository.findById(id)
                .map(appointment -> {
                    appointmentRepository.delete(appointment);
                    return appointment;
                });
    }
}
