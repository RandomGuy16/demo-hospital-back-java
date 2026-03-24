CREATE TABLE appointments
(
    appointment_id  UUID                        NOT NULL,
    patient_id      UUID                        NOT NULL,
    practitioner_id UUID                        NOT NULL,
    department_id   UUID                        NOT NULL,
    start_time      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status          VARCHAR(10)                 NOT NULL,
    CONSTRAINT pk_appointments PRIMARY KEY (appointment_id)
);

CREATE TABLE department_practitioners
(
    department_id   UUID NOT NULL,
    practitioner_id UUID NOT NULL
);

CREATE TABLE departments
(
    department_id UUID                        NOT NULL,
    name          VARCHAR(50)                 NOT NULL,
    description   VARCHAR(200)                NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_departments PRIMARY KEY (department_id)
);

CREATE TABLE patients
(
    patient_id    UUID                        NOT NULL,
    first_name    VARCHAR(50)                 NOT NULL,
    last_name     VARCHAR(50)                 NOT NULL,
    id_number     VARCHAR(10)                 NOT NULL,
    date_of_birth date                        NOT NULL,
    gender        VARCHAR(20),
    phone_number  VARCHAR(20)                 NOT NULL,
    contacts      VARCHAR(200),
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    mrn           VARCHAR(50)                 NOT NULL,
    address       VARCHAR(200)                NOT NULL,
    CONSTRAINT pk_patients PRIMARY KEY (patient_id)
);

CREATE TABLE practitioner_departments
(
    department_id   UUID NOT NULL,
    practitioner_id UUID NOT NULL
);

CREATE TABLE practitioner_specialties
(
    practitioner_id UUID NOT NULL,
    specialty       VARCHAR(100)
);

CREATE TABLE practitioners
(
    practitioner_id UUID                        NOT NULL,
    first_name      VARCHAR(50)                 NOT NULL,
    last_name       VARCHAR(50)                 NOT NULL,
    id_number       VARCHAR(10)                 NOT NULL,
    date_of_birth   date                        NOT NULL,
    gender          VARCHAR(20),
    phone_number    VARCHAR(20)                 NOT NULL,
    contacts        VARCHAR(200),
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_practitioners PRIMARY KEY (practitioner_id)
);

ALTER TABLE departments
    ADD CONSTRAINT uc_departments_name UNIQUE (name);

ALTER TABLE patients
    ADD CONSTRAINT uc_patients_id_number UNIQUE (id_number);

ALTER TABLE patients
    ADD CONSTRAINT uc_patients_mrn UNIQUE (mrn);

ALTER TABLE practitioners
    ADD CONSTRAINT uc_practitioners_id_number UNIQUE (id_number);

ALTER TABLE appointments
    ADD CONSTRAINT FK_APPOINTMENTS_ON_DEPARTMENT FOREIGN KEY (department_id) REFERENCES departments (department_id);

ALTER TABLE appointments
    ADD CONSTRAINT FK_APPOINTMENTS_ON_PATIENT FOREIGN KEY (patient_id) REFERENCES patients (patient_id);

ALTER TABLE appointments
    ADD CONSTRAINT FK_APPOINTMENTS_ON_PRACTITIONER FOREIGN KEY (practitioner_id) REFERENCES practitioners (practitioner_id);

ALTER TABLE department_practitioners
    ADD CONSTRAINT fk_deppra_on_department FOREIGN KEY (department_id) REFERENCES departments (department_id);

ALTER TABLE department_practitioners
    ADD CONSTRAINT fk_deppra_on_practitioner FOREIGN KEY (practitioner_id) REFERENCES practitioners (practitioner_id);

ALTER TABLE practitioner_specialties
    ADD CONSTRAINT fk_practitioner_specialties_on_practitioner FOREIGN KEY (practitioner_id) REFERENCES practitioners (practitioner_id);

ALTER TABLE practitioner_departments
    ADD CONSTRAINT fk_pradep_on_department FOREIGN KEY (department_id) REFERENCES departments (department_id);

ALTER TABLE practitioner_departments
    ADD CONSTRAINT fk_pradep_on_practitioner FOREIGN KEY (practitioner_id) REFERENCES practitioners (practitioner_id);
