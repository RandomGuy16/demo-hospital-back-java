CREATE TABLE persons
(
    person_id    UUID                        NOT NULL,
    first_name   VARCHAR(50),
    last_name    VARCHAR(50),
    id_number    VARCHAR(10)                 NOT NULL,
    date_of_birth DATE,
    gender       VARCHAR(20),
    phone_number VARCHAR(20)                 NOT NULL,
    contacts     VARCHAR(200),
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_persons PRIMARY KEY (person_id)
);

INSERT INTO persons (person_id, first_name, last_name, id_number, date_of_birth, gender, phone_number, contacts, created_at, updated_at)
SELECT patient_id, first_name, last_name, id_number, date_of_birth, gender, phone_number, contacts, created_at, updated_at
FROM patients;

INSERT INTO persons (person_id, first_name, last_name, id_number, date_of_birth, gender, phone_number, contacts, created_at, updated_at)
SELECT practitioner_id, first_name, last_name, id_number, date_of_birth, gender, phone_number, contacts, created_at, updated_at
FROM practitioners
ON CONFLICT (person_id) DO NOTHING;

ALTER TABLE patients
    DROP COLUMN first_name,
    DROP COLUMN last_name,
    DROP COLUMN id_number,
    DROP COLUMN date_of_birth,
    DROP COLUMN gender,
    DROP COLUMN phone_number,
    DROP COLUMN contacts,
    DROP COLUMN created_at,
    DROP COLUMN updated_at;

ALTER TABLE patients
    ADD CONSTRAINT fk_patients_on_person FOREIGN KEY (patient_id) REFERENCES persons (person_id) ON DELETE CASCADE;

ALTER TABLE practitioners
    DROP COLUMN first_name,
    DROP COLUMN last_name,
    DROP COLUMN id_number,
    DROP COLUMN date_of_birth,
    DROP COLUMN gender,
    DROP COLUMN phone_number,
    DROP COLUMN contacts,
    DROP COLUMN created_at,
    DROP COLUMN updated_at;

ALTER TABLE practitioners
    ADD CONSTRAINT fk_practitioners_on_person FOREIGN KEY (practitioner_id) REFERENCES persons (person_id) ON DELETE CASCADE;
