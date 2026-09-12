ALTER TABLE appointments
    ADD COLUMN chief_complaint VARCHAR(128) NOT NULL DEFAULT 'Not specified',
    ADD COLUMN triage_urgency  VARCHAR(10)  NOT NULL DEFAULT 'ROUTINE',
    ADD COLUMN triage_notes    VARCHAR(128);

ALTER TABLE appointments
    ALTER COLUMN chief_complaint DROP DEFAULT,
    ALTER COLUMN triage_urgency DROP DEFAULT;

ALTER TABLE appointments
    ADD CONSTRAINT chk_appointments_triage_urgency
        CHECK (triage_urgency IN ('ROUTINE', 'URGENT', 'EMERGENCY'));
