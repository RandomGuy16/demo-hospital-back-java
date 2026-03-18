package com.example.demo.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.UUID;
import java.time.LocalDateTime;

@Schema(name = "Appointment", description = "Appointment record")
@Entity
@Table(name = "appointments")
public class Appointment {
    // Appointment: id, patientId, practitionerId, departmentId, start, end, status

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "de305d54-75b4-431b-adb2-eb6b9e546014")
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "appointment_id")
    private UUID appointmentId;

    @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    @JoinColumn(name = "patient_id")
    private UUID patientId;

    @Schema(example = "d2719c5d-84d1-43f6-a713-eef8a694be75")
    @JoinColumn(name = "practitioner_id")
    private UUID practitionerId;

    @Schema(example = "a0b1f54e-98c4-4e4d-9412-2eaf3e0c8695")
    @JoinColumn(name = "department_id")
    private UUID departmentId;

    @Schema(example = "2026-04-10T09:00:00")
    @Column(nullable = false, name = "start_time")
    private LocalDateTime start;

    @Schema(example = "2026-04-10T09:30:00")
    @Column(nullable = false, name = "end_time")
    private LocalDateTime end;

    @Schema(example = "SCHEDULED")
    @Column(nullable = false, length = 10)
    private String status;

    public Appointment() {}

    public Appointment(UUID patientId, UUID practitionerId, UUID departmentId, LocalDateTime start, LocalDateTime end, String status) {
        this.patientId = patientId;
        this.practitionerId = practitionerId;
        this.departmentId = departmentId;
        this.start = start;
        this.end = end;
        this.status = status;
    }

    public UUID getAppointmentId() {
        return appointmentId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public UUID getPractitionerId() {
        return practitionerId;
    }

    public void setPractitionerId(UUID practitionerId) {
        this.practitionerId = practitionerId;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(UUID departmentId) {
        this.departmentId = departmentId;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
