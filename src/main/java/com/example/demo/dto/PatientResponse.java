package com.example.demo.dto;

import java.time.LocalDate;
import java.util.UUID;

public record PatientResponse (
    UUID patientId,
    String firstName,
    String lastName,
    LocalDate dateOfBirth,
    String gender,
    String phoneNumber,
    String contacts,
    String mrn,
    String address
) {}
