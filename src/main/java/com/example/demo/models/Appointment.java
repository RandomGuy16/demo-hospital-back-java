package com.example.demo.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.UUID;
import java.time.LocalDateTime;

@ValidAppointmentTime
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", nullable = false)
    private Patient patient;

    @Schema(example = "d2719c5d-84d1-43f6-a713-eef8a694be75")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practitioner_id", referencedColumnName = "practitioner_id", nullable = false)
    private Practitioner practitioner;

    @Schema(example = "a0b1f54e-98c4-4e4d-9412-2eaf3e0c8695")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", referencedColumnName = "department_id", nullable = false)
    private Department department;

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

    public Appointment(Patient patient, Practitioner practitioner, Department department, LocalDateTime start, LocalDateTime end, String status) {
        this.patient = patient;
        this.practitioner = practitioner;
        this.department = department;
        this.start = start;
        this.end = end;
        this.status = status;
    }

    public UUID getAppointmentId() {
        return appointmentId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Practitioner getPractitioner() {
        return practitioner;
    }

    public void setPractitioner(Practitioner practitioner) {
        this.practitioner = practitioner;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
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
