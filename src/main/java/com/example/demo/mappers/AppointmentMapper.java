package com.example.demo.mappers;

import com.example.demo.dto.AppointmentResponse;
import com.example.demo.models.Appointment;

public class AppointmentMapper {

    public static AppointmentResponse appointmentToAppointmentResponse(Appointment appointment) {
        return new AppointmentResponse(
            appointment.getAppointmentId(),
            appointment.getPatientId(),
            appointment.getPractitionerId(),
            appointment.getDepartmentId(),
            appointment.getStart(),
            appointment.getEnd(),
            appointment.getStatus());
    }

}
