package com.example.demo.services;

import com.example.demo.dto.AppointmentRequest;
import com.example.demo.errors.RepeatedIdNumberError;
import com.example.demo.errors.ResourceNotFoundException;
import com.example.demo.errors.SelfDiagnosisConflictException;
import com.example.demo.models.Appointment;
import com.example.demo.models.Department;
import com.example.demo.models.Patient;
import com.example.demo.models.Practitioner;
import com.example.demo.repositories.AppointmentRepository;
import com.example.demo.repositories.DepartmentRepository;
import com.example.demo.repositories.PatientRepository;
import com.example.demo.repositories.PractitionerRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final PractitionerRepository practitionerRepository;
    private final DepartmentRepository departmentRepository;

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

    private AppointmentRefs validateAppointment(AppointmentRequest request) throws ResourceNotFoundException {
        // findById().orElseThrow() is one query but loads the whole model
        // existsById() and getReferenceById() is lightweight but 2 roundtrips to database

        Patient patient = patientRepository.findById(request.patientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Practitioner practitioner = practitionerRepository.findById(request.practitionerId())
            .orElseThrow(() -> new ResourceNotFoundException("Practitioner not found"));

        Department department =  departmentRepository.findById(request.departmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

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
        // Validate that the patient, practitioner, and department exist before creating the appointment
        AppointmentRefs payload = validateAppointment(request);

        // create the appointment
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
        // create the appointment
        AppointmentRefs payload = validateAppointment(request);

        return appointmentRepository.findById(id)
                .map(appointment -> {
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
