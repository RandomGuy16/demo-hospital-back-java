package com.evergreen.generalhospital.mappers;

import com.evergreen.generalhospital.dto.appointment.AppointmentResponse;
import com.evergreen.generalhospital.models.appointment.Appointment;

public class AppointmentMapper {

    public static AppointmentResponse appointmentToAppointmentResponse(Appointment appointment) {
        return new AppointmentResponse(
            appointment.getAppointmentId(),
            appointment.getPatient().getPatientId(),
            appointment.getPractitioner().getPractitionerId(),
            appointment.getDepartment().getDepartmentId(),
            appointment.getChiefComplaint(),
            appointment.getStart(),
            appointment.getEnd(),
            appointment.getStatus());
    }

}
