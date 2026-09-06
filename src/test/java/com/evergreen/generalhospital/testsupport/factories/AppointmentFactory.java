package com.evergreen.generalhospital.testsupport.factories;

import com.evergreen.generalhospital.models.appointment.Appointment;
import com.evergreen.generalhospital.models.appointment.AppointmentStatus;
import com.evergreen.generalhospital.models.appointment.UrgencyLevel;
import com.evergreen.generalhospital.models.department.Department;
import com.evergreen.generalhospital.models.patient.Patient;
import com.evergreen.generalhospital.models.practitioner.Practitioner;
import com.evergreen.generalhospital.repositories.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import java.time.LocalDateTime;

@TestComponent
public class AppointmentFactory {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public Appointment saveAppointment(Patient patient,
                                       Practitioner practitioner,
                                       Department department,
                                       String status) {
        return saveAppointment(patient, practitioner, department, AppointmentStatus.valueOf(status));
    }

    public Appointment saveAppointment(Patient patient,
                                       Practitioner practitioner,
                                       Department department,
                                       AppointmentStatus status) {
        return saveAppointment(patient, practitioner, department, "Routine checkup", status);
    }

    public Appointment saveAppointment(Patient patient,
                                       Practitioner practitioner,
                                       Department department,
                                       String chiefComplaint,
                                       AppointmentStatus status) {
        LocalDateTime start = LocalDateTime.now().plusDays(5);
        Appointment appointment = new Appointment(
            patient,
            practitioner,
            department,
            start,
            start.plusMinutes(30),
            status,
            chiefComplaint,
            UrgencyLevel.ROUTINE,
            null
        );
        return appointmentRepository.save(appointment);
    }

}
