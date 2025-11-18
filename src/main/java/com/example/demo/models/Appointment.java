package com.example.demo.models;

import jakarta.persistence.*;

import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {
    // Appointment: id, patientId, practitionerId, departmentId, start, end, status

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "appointment_id")
    private UUID appointmentId;

    @JoinColumn(name = "patient_id")
    private UUID patientId;

    @JoinColumn(name = "practitioner_id")
    private UUID practitionerId;

    @JoinColumn(name = "department_id")
    private UUID departmentId;

    @Column(nullable = false, name = "start_time")
    private LocalDateTime start;

    @Column(nullable = false, name = "end_time")
    private LocalDateTime end;

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
}
