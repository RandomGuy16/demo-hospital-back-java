package com.example.demo.testsupport.factories;

import com.example.demo.models.appointment.Appointment;
import com.example.demo.models.appointment.AppointmentStatus;
import com.example.demo.models.department.Department;
import com.example.demo.models.patient.Patient;
import com.example.demo.models.practitioner.Practitioner;
import com.example.demo.repositories.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import java.time.LocalDateTime;

@TestComponent
public class AppointmentFactory {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public Appointment saveAppointment(Patient patient, Practitioner practitioner, Department department, String status) {
        return saveAppointment(patient, practitioner, department, AppointmentStatus.valueOf(status));
    }

    public Appointment saveAppointment(Patient patient,
                                          Practitioner practitioner,
                                          Department department,
                                          AppointmentStatus status) {
        LocalDateTime start = LocalDateTime.now().plusDays(5);
        Appointment appointment = new Appointment(
            patient,
            practitioner,
            department,
            start,
            start.plusMinutes(30),
            status
        );
        return appointmentRepository.save(appointment);
    }

}
