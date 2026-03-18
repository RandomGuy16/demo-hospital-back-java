package com.example.demo.services;

import com.example.demo.dto.AppointmentRequest;
import com.example.demo.models.Appointment;
import com.example.demo.repositories.AppointmentRepository;
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

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public Appointment createAppointment(AppointmentRequest request) {
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
