package com.example.demo.models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MedicalRecord", description = "Placeholder medical record model for future phases")
public class MedicalRecord {
    // id, patientId, encounters, diagnoses, medications (future expansion)
    private int id;
    private int patientId;
    private String encounters;
    private String diagnoses;
    private String medications;

    public MedicalRecord(int id, int patientId, String encounters, String diagnoses, String medications) {
        this.id = id;
        this.patientId = patientId;
        this.encounters = encounters;
    }
}
