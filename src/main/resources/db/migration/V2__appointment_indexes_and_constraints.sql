CREATE INDEX idx_appointments_patient_time
    ON appointments (patient_id, start_time, end_time);

CREATE INDEX idx_appointments_practitioner_time
    ON appointments (practitioner_id, start_time, end_time);

ALTER TABLE appointments
    ADD CONSTRAINT chk_appointments_status
        CHECK (status IN ('SCHEDULED', 'COMPLETED', 'CANCELLED'));

ALTER TABLE practitioner_specialties
    ALTER COLUMN specialty SET NOT NULL;

ALTER TABLE practitioner_specialties
    ADD CONSTRAINT pk_practitioner_specialties PRIMARY KEY (practitioner_id, specialty);
