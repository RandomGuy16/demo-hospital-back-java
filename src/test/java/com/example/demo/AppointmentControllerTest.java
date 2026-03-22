package com.example.demo;

import com.example.demo.dto.AppointmentRequest;
import com.example.demo.errors.ErrorCode;
import com.example.demo.models.Appointment;
import com.example.demo.models.Patient;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AppointmentControllerTest extends ControllerTestSupport {

    @Test
    void createAppointmentReturnsCreatedResponse() throws Exception {

        AppointmentRequest request = new AppointmentRequest(
                defaultSubjects.patient().getPatientId(),
                defaultSubjects.practitioner().getPractitionerId(),
                defaultSubjects.department().getDepartmentId(),
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusMinutes(45),
                "SCHEDULED");

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.appointmentId").isNotEmpty())
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    void createAppointmentWithNonExistingSubjectsReturnsNotFound() throws Exception {
        AppointmentRequest request = new AppointmentRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1),
                "SCHEDULED");

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.NOT_FOUND.name()));
    }

    @Test
    void createAppointmentWithInvalidPayloadReturnsBadRequest() throws Exception {
        AppointmentRequest request = new AppointmentRequest(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(1),
            "");

        mockMvc.perform(post("/api/v1/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.name()));
    }

    @Test
    void createAppointmentWithSamePatientAndPractitionerReturnsConflict() throws Exception {
        // basically you can't attend yourself test
        // first create a patient for the practitioner
        Patient ppatient = savePatient(
            defaultSubjects.practitioner().getFirstName(),
            defaultSubjects.practitioner().getLastName(),
            defaultSubjects.practitioner().getIdNumber()
        );

        AppointmentRequest request = new AppointmentRequest(
            ppatient.getPatientId(),
            defaultSubjects.practitioner().getPractitionerId(),
            defaultSubjects.department().getDepartmentId(),
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(1).plusHours(1),
            "SCHEDULED");

        mockMvc.perform(post("/api/v1/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value(ErrorCode.CONFLICT.name()));
    }

    @Test
    void getAllAppointmentsReturnsSortedPage() throws Exception {
        saveAppointment(
            defaultSubjects.patient(),
            defaultSubjects.practitioner(),
            defaultSubjects.department(),
            "SCHEDULED");
        saveAppointment(
            funnySubjects.patient(),
            funnySubjects.practitioner(),
            funnySubjects.department(),
            "COMPLETED"
        );

        mockMvc.perform(get("/api/v1/appointments")
                        .param("sort", "status,asc")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].status").value("COMPLETED"))
                .andExpect(jsonPath("$.content[1].status").value("SCHEDULED"));
    }

    @Test
    void getAppointmentByIdReturnsAppointment() throws Exception {
        Appointment appointment = saveAppointment(
            defaultSubjects.patient(),
            defaultSubjects.practitioner(),
            defaultSubjects.department(),
            "SCHEDULED");

        mockMvc.perform(get("/api/v1/appointments/{id}", appointment.getAppointmentId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(appointment.getAppointmentId().toString()))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    void updateAppointmentReturnsUpdatedAppointment() throws Exception {
        Appointment appointment = saveAppointment(
            defaultSubjects.patient(),
            defaultSubjects.practitioner(),
            defaultSubjects.department(),
            "SCHEDULED");

        AppointmentRequest request = new AppointmentRequest(
                funnySubjects.patient().getPatientId(),
                funnySubjects.practitioner().getPractitionerId(),
                funnySubjects.department().getDepartmentId(),
                LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(10).plusMinutes(30),
                "COMPLETED");

        mockMvc.perform(put("/api/v1/appointments/{id}", appointment.getAppointmentId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(appointment.getAppointmentId().toString()))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void deleteAppointmentReturnsDeletedPayload() throws Exception {
        Appointment appointment = saveAppointment(
            funnySubjects.patient(),
            funnySubjects.practitioner(),
            funnySubjects.department(),
            "SCHEDULED");

        mockMvc.perform(delete("/api/v1/appointments/{id}", appointment.getAppointmentId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAppointmentByIdReturnsNotFoundWhenMissing() throws Exception {
        mockMvc.perform(get("/api/v1/appointments/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
