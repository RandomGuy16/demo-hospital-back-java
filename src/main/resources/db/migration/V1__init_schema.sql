-- ============================================================================
-- V1: Squashed Schema Migration
-- ============================================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ----------------------------------------------------------------------------
-- Tables
-- ----------------------------------------------------------------------------

CREATE TABLE persons (
    person_id uuid NOT NULL,
    first_name character varying(50),
    last_name character varying(50),
    id_number character varying(10) NOT NULL,
    date_of_birth date,
    gender character varying(20),
    phone_number character varying(20) NOT NULL,
    emergency_contact character varying(200),
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);

CREATE TABLE departments (
    department_id uuid NOT NULL,
    name character varying(50) NOT NULL,
    description character varying(200) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);

CREATE TABLE patients (
    patient_id uuid NOT NULL,
    mrn character varying(50) NOT NULL,
    address character varying(200) NOT NULL
);

CREATE TABLE practitioners (
    practitioner_id uuid NOT NULL
);

CREATE TABLE practitioner_specialties (
    practitioner_id uuid NOT NULL,
    specialty character varying(100) NOT NULL
);

CREATE TABLE department_practitioners (
    department_id uuid NOT NULL,
    practitioner_id uuid NOT NULL
);

CREATE TABLE appointments (
    appointment_id uuid NOT NULL,
    patient_id uuid NOT NULL,
    practitioner_id uuid NOT NULL,
    department_id uuid NOT NULL,
    start_time timestamp without time zone NOT NULL,
    end_time timestamp without time zone NOT NULL,
    status character varying(10) NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    chief_complaint character varying(128) NOT NULL,
    triage_urgency character varying(10) NOT NULL,
    triage_notes character varying(128),
    CONSTRAINT chk_appointments_status CHECK (((status)::text = ANY ((ARRAY['SCHEDULED'::character varying, 'COMPLETED'::character varying, 'CANCELLED'::character varying])::text[]))),
    CONSTRAINT chk_appointments_time_range CHECK ((start_time < end_time)),
    CONSTRAINT chk_appointments_triage_urgency CHECK (((triage_urgency)::text = ANY ((ARRAY['ROUTINE'::character varying, 'URGENT'::character varying, 'EMERGENCY'::character varying])::text[])))
);

CREATE TABLE user_accounts (
    user_id uuid NOT NULL,
    provider character varying(50) NOT NULL,
    provider_subject character varying(100) NOT NULL,
    display_name character varying(50) NOT NULL,
    username character varying(50) NOT NULL,
    email character varying(200) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    password_hash character varying(1000),
    person_id uuid
);

CREATE TABLE user_roles (
    user_id uuid NOT NULL,
    role character varying(50) NOT NULL,
    CONSTRAINT chk_user_roles_role CHECK (((role)::text = ANY ((ARRAY['ROLE_ADMIN'::character varying, 'ROLE_RECEPTIONIST'::character varying, 'ROLE_PATIENT'::character varying, 'ROLE_PRACTITIONER'::character varying])::text[])))
);

-- ----------------------------------------------------------------------------
-- Primary Keys
-- ----------------------------------------------------------------------------

ALTER TABLE ONLY persons
    ADD CONSTRAINT pk_persons PRIMARY KEY (person_id);

ALTER TABLE ONLY departments
    ADD CONSTRAINT pk_departments PRIMARY KEY (department_id);

ALTER TABLE ONLY patients
    ADD CONSTRAINT pk_patients PRIMARY KEY (patient_id);

ALTER TABLE ONLY practitioners
    ADD CONSTRAINT pk_practitioners PRIMARY KEY (practitioner_id);

ALTER TABLE ONLY practitioner_specialties
    ADD CONSTRAINT pk_practitioner_specialties PRIMARY KEY (practitioner_id, specialty);

ALTER TABLE ONLY department_practitioners
    ADD CONSTRAINT pk_department_practitioners PRIMARY KEY (department_id, practitioner_id);

ALTER TABLE ONLY appointments
    ADD CONSTRAINT pk_appointments PRIMARY KEY (appointment_id);

ALTER TABLE ONLY user_accounts
    ADD CONSTRAINT pk_user_accounts PRIMARY KEY (user_id);

ALTER TABLE ONLY user_roles
    ADD CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role);

-- ----------------------------------------------------------------------------
-- Unique Constraints
-- ----------------------------------------------------------------------------

ALTER TABLE ONLY departments
    ADD CONSTRAINT uc_departments_name UNIQUE (name);

ALTER TABLE ONLY patients
    ADD CONSTRAINT uc_patients_mrn UNIQUE (mrn);

ALTER TABLE ONLY user_accounts
    ADD CONSTRAINT uc_user_accounts_email UNIQUE (email);

ALTER TABLE ONLY user_accounts
    ADD CONSTRAINT uc_user_accounts_person UNIQUE (person_id);

ALTER TABLE ONLY user_accounts
    ADD CONSTRAINT uc_user_accounts_provider_subject UNIQUE (provider, provider_subject);

ALTER TABLE ONLY user_accounts
    ADD CONSTRAINT uc_user_accounts_username UNIQUE (username);

-- ----------------------------------------------------------------------------
-- Indexes
-- ----------------------------------------------------------------------------

CREATE INDEX idx_appointments_patient_time ON appointments USING btree (patient_id, start_time, end_time);

CREATE INDEX idx_appointments_practitioner_time ON appointments USING btree (practitioner_id, start_time, end_time);

-- ----------------------------------------------------------------------------
-- Foreign Keys
-- ----------------------------------------------------------------------------

ALTER TABLE ONLY patients
    ADD CONSTRAINT fk_patients_on_person FOREIGN KEY (patient_id) REFERENCES persons(person_id) ON DELETE CASCADE;

ALTER TABLE ONLY practitioners
    ADD CONSTRAINT fk_practitioners_on_person FOREIGN KEY (practitioner_id) REFERENCES persons(person_id) ON DELETE CASCADE;

ALTER TABLE ONLY practitioner_specialties
    ADD CONSTRAINT fk_practitioner_specialties_on_practitioner FOREIGN KEY (practitioner_id) REFERENCES practitioners(practitioner_id);

ALTER TABLE ONLY department_practitioners
    ADD CONSTRAINT fk_deppra_on_department FOREIGN KEY (department_id) REFERENCES departments(department_id);

ALTER TABLE ONLY department_practitioners
    ADD CONSTRAINT fk_deppra_on_practitioner FOREIGN KEY (practitioner_id) REFERENCES practitioners(practitioner_id);

ALTER TABLE ONLY appointments
    ADD CONSTRAINT fk_appointments_on_department FOREIGN KEY (department_id) REFERENCES departments(department_id);

ALTER TABLE ONLY appointments
    ADD CONSTRAINT fk_appointments_on_patient FOREIGN KEY (patient_id) REFERENCES patients(patient_id);

ALTER TABLE ONLY appointments
    ADD CONSTRAINT fk_appointments_on_practitioner FOREIGN KEY (practitioner_id) REFERENCES practitioners(practitioner_id);

ALTER TABLE ONLY user_accounts
    ADD CONSTRAINT fk_user_accounts_person FOREIGN KEY (person_id) REFERENCES persons(person_id);

ALTER TABLE ONLY user_roles
    ADD CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES user_accounts(user_id) ON DELETE CASCADE;
