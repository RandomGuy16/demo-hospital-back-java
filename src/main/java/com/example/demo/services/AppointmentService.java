package com.example.demo.services;

import com.example.demo.dto.AppointmentRequest;
import com.example.demo.errors.AppointmentCollisionException;
import com.example.demo.errors.ResourceNotFoundException;
import com.example.demo.errors.SelfDiagnosisConflictException;
import com.example.demo.models.appointment.Appointment;
import com.example.demo.models.department.Department;
import com.example.demo.models.patient.Patient;
import com.example.demo.models.practitioner.Practitioner;
import com.example.demo.repositories.AppointmentRepository;
import com.example.demo.repositories.DepartmentRepository;
import com.example.demo.repositories.PatientRepository;
import com.example.demo.repositories.PractitionerRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class AppointmentService {
    // repositories direct links
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final PractitionerRepository practitionerRepository;
    private final DepartmentRepository departmentRepository;

    // simple record to temporarily store the input after validating
    private record AppointmentRefs (
        Patient patient,
        Practitioner practitioner,
        Department department
    ) { }


    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              PractitionerRepository practitionerRepository,
                              DepartmentRepository departmentRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.practitionerRepository = practitionerRepository;
        this.departmentRepository = departmentRepository;
    }

    // method for validating the no collision of an appointment
    private void validateAppointmentNoCollision(AppointmentRequest request) throws AppointmentCollisionException {
        boolean doesPatientHaveCollision = appointmentRepository
            .existsByPatient_PatientIdAndStartBeforeAndEndAfter(request.patientId(), request.end(), request.start());

        boolean doesPractitionerHaveCollision = appointmentRepository
            .existsByPractitioner_PractitionerIdAndStartBeforeAndEndAfter(request.practitionerId(), request.end(), request.start());

        if (doesPatientHaveCollision || doesPractitionerHaveCollision)
            throw new AppointmentCollisionException("Appointment time collision detected");
    }

    // same as before but for update requests
    private void validateAppointmentNoCollision(UUID appointmentId, AppointmentRequest request) throws AppointmentCollisionException {
        boolean doesPatientHaveCollision = appointmentRepository
            .existsByPatient_PatientIdAndAppointmentIdNotAndStartBeforeAndEndAfter(
                request.patientId(),
                appointmentId,
                request.end(),
                request.start()
            );

        boolean doesPractitionerHaveCollision = appointmentRepository
            .existsByPractitioner_PractitionerIdAndAppointmentIdNotAndStartBeforeAndEndAfter(
                request.practitionerId(),
                appointmentId,
                request.end(),
                request.start()
            );

        if (doesPatientHaveCollision || doesPractitionerHaveCollision) {
            throw new AppointmentCollisionException("Appointment time collision detected");
        }
    }

    private AppointmentRefs validateAppointmentParticipantsExistence(AppointmentRequest request) throws ResourceNotFoundException {
        // findById().orElseThrow() is one query but loads the whole model
        // existsById() and getReferenceById() is lightweight but 2 roundtrips to database

        Patient patient = patientRepository.findById(request.patientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Practitioner practitioner = practitionerRepository.findById(request.practitionerId())
            .orElseThrow(() -> new ResourceNotFoundException("Practitioner not found"));

        Department department =  departmentRepository.findById(request.departmentId())
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
        AppointmentRefs payload = validateAppointmentParticipantsExistence(request);
        validateAppointmentNoCollision(request);

        Appointment appointment = new Appointment(
                payload.patient,
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
                    AppointmentRefs payload = validateAppointmentParticipantsExistence(request);
                    validateAppointmentNoCollision(id, request);

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
