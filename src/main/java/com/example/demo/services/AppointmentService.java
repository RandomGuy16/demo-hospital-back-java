package com.example.demo.services;

import com.example.demo.dto.AppointmentRequest;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.models.Appointment;
import com.example.demo.repositories.AppointmentRepository;
import com.example.demo.repositories.PatientRepository;
import com.example.demo.repositories.PractitionerRepository;
import com.example.demo.repositories.DepartmentRepository;
import com.example.demo.services.PractitionerService;
import com.example.demo.services.PatientService;
import com.example.demo.services.DepartmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientService patientService;
    private final PractitionerService practitionerService;
    private final DepartmentService departmentService;


    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientService patientService,
                              PractitionerService practitionerService,
                              DepartmentService departmentService) {
        this.appointmentRepository = appointmentRepository;
        this.patientService = patientService;
        this.practitionerService = practitionerService;
        this.departmentService = departmentService;
    }

    public Appointment createAppointment(AppointmentRequest request) {
        // Validate that the patient, practitioner, and department exist before creating the appointment
        patientService.getPatientById(request.patientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        practitionerService.getPractitionerById(request.practitionerId())
            .orElseThrow(() -> new ResourceNotFoundException("Practitioner not found"));
        departmentService.getDepartmentById(request.departmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        // create the appointment
        Appointment appointment = new Appointment(
                request.patientId(),
                request.practitionerId(),
                request.departmentId(),
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
                    appointment.setPatientId(request.patientId());
                    appointment.setPractitionerId(request.practitionerId());
                    appointment.setDepartmentId(request.departmentId());
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
