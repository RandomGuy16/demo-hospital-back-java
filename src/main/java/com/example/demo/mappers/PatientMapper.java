package com.example.demo.mappers;

import com.example.demo.dto.PatientResponse;
import com.example.demo.models.patient.Patient;

public class PatientMapper {

    public static PatientResponse patientToPatientResponse(Patient patient) {
        return new PatientResponse(
            patient.getPatientId(),
            patient.getFirstName(),
            patient.getLastName(),
            patient.getDateOfBirth(),
            patient.getGender(),
            patient.getPhoneNumber(),
            patient.getContacts(),
            patient.getMrn(),
            patient.getAddress());
    }
}
