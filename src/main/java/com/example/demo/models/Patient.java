package com.example.demo.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Schema(name = "Patient", description = "Patient record")
@Entity
@Table(name = "patients")
public class Patient extends Person {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "patient_id")
    private UUID patientId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "ShokoIeiri-1234567890ABCDEF12")
    @Column(nullable = false, unique = true, length = 50)
    private String mrn;  // Medical Record Number

    @Schema(example = "123 Main St, Springfield")
    @Column(nullable = false, length = 200)
    private String address;

    // Constructors
    public Patient() {
        super();
    }

    public Patient(
        String firstName,
        String lastName,
        String idNumber,
        LocalDate dateOfBirth,
        String gender,
        String phoneNumber,
        String contacts,
        String mrn,
        String address) {
        super(firstName, lastName, idNumber, dateOfBirth, gender, phoneNumber, contacts);
        this.mrn = mrn;
        this.address = address;
    }

    // Getters and Setters
    public UUID getPatientId() {
        return patientId;
    }

    public String getMrn() {
        return mrn;
    }

    public void setMrn(String mrn) {
        this.mrn = mrn;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
