package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PatientRequest(
        @NotBlank String firstName,

        @NotBlank String lastName,

        @NotBlank @Size(min = 10, max = 10) String idNumber,

        @NotNull @Past(message = "dateOfBirth must be in the past") LocalDate dateOfBirth,

        @NotBlank String gender,

        @NotBlank @Size(min = 6, max = 20) String phoneNumber,

        @NotBlank String contacts,

        @NotBlank String address) {
}
