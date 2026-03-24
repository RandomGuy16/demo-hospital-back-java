package com.example.demo.mappers;

import com.example.demo.dto.AppointmentResponse;
import com.example.demo.models.appointment.Appointment;

public class AppointmentMapper {

    public static AppointmentResponse appointmentToAppointmentResponse(Appointment appointment) {
        return new AppointmentResponse(
            appointment.getAppointmentId(),
            appointment.getPatient().getPatientId(),
            appointment.getPractitioner().getPractitionerId(),
            appointment.getDepartment().getDepartmentId(),
            appointment.getStart(),
            appointment.getEnd(),
            appointment.getStatus());
    }

}
