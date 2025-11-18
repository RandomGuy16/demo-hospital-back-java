package com.example.demo.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "patients")
public class Patient extends Person {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "patient_id")
    private UUID patientId;

    @Column(nullable = false, unique = true, length = 50)
    private String mrn;  // Medical Record Number

    @Column(nullable = false, length = 200)
    private String address;

    // Constructors
    public Patient() {
        super();
    }

    public Patient(String firstName, String lastName, LocalDate dateOfBirth,
                   String gender, String phoneNumber, String contacts,
                   String mrn, String address) {
        super(firstName, lastName, dateOfBirth, gender, phoneNumber, contacts);
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
