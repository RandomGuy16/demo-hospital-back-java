package com.evergreen.generalhospital.mappers;

import com.evergreen.generalhospital.dto.PatientResponse;
import com.evergreen.generalhospital.models.patient.Patient;

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
