--
-- PostgreSQL database dump
--
-- Dumped from database version 16.13
-- Dumped by pg_dump version 16.13

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: pgcrypto; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;


--
-- Name: EXTENSION pgcrypto; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: appointments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.appointments (
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


--
-- Name: department_practitioners; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.department_practitioners (
    department_id uuid NOT NULL,
    practitioner_id uuid NOT NULL
);


--
-- Name: departments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.departments (
    department_id uuid NOT NULL,
    name character varying(50) NOT NULL,
    description character varying(200) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);



--
-- Name: patients; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.patients (
    patient_id uuid NOT NULL,
    mrn character varying(50) NOT NULL,
    address character varying(200) NOT NULL
);


--
-- Name: persons; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.persons (
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


--
-- Name: practitioner_specialties; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.practitioner_specialties (
    practitioner_id uuid NOT NULL,
    specialty character varying(100) NOT NULL
);


--
-- Name: practitioners; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.practitioners (
    practitioner_id uuid NOT NULL
);


--
-- Name: user_accounts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_accounts (
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


--
-- Name: user_roles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_roles (
    user_id uuid NOT NULL,
    role character varying(50) NOT NULL,
    CONSTRAINT chk_user_roles_role CHECK (((role)::text = ANY ((ARRAY['ROLE_ADMIN'::character varying, 'ROLE_RECEPTIONIST'::character varying, 'ROLE_PATIENT'::character varying, 'ROLE_PRACTITIONER'::character varying])::text[])))
);



--
-- Name: appointments pk_appointments; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.appointments
    ADD CONSTRAINT pk_appointments PRIMARY KEY (appointment_id);


--
-- Name: department_practitioners pk_department_practitioners; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.department_practitioners
    ADD CONSTRAINT pk_department_practitioners PRIMARY KEY (department_id, practitioner_id);


--
-- Name: departments pk_departments; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.departments
    ADD CONSTRAINT pk_departments PRIMARY KEY (department_id);


--
-- Name: patients pk_patients; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.patients
    ADD CONSTRAINT pk_patients PRIMARY KEY (patient_id);


--
-- Name: persons pk_persons; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.persons
    ADD CONSTRAINT pk_persons PRIMARY KEY (person_id);


--
-- Name: practitioner_specialties pk_practitioner_specialties; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.practitioner_specialties
    ADD CONSTRAINT pk_practitioner_specialties PRIMARY KEY (practitioner_id, specialty);


--
-- Name: practitioners pk_practitioners; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.practitioners
    ADD CONSTRAINT pk_practitioners PRIMARY KEY (practitioner_id);


--
-- Name: user_accounts pk_user_accounts; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_accounts
    ADD CONSTRAINT pk_user_accounts PRIMARY KEY (user_id);


--
-- Name: user_roles pk_user_roles; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role);


--
-- Name: departments uc_departments_name; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.departments
    ADD CONSTRAINT uc_departments_name UNIQUE (name);


--
-- Name: patients uc_patients_mrn; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.patients
    ADD CONSTRAINT uc_patients_mrn UNIQUE (mrn);


--
-- Name: user_accounts uc_user_accounts_email; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_accounts
    ADD CONSTRAINT uc_user_accounts_email UNIQUE (email);


--
-- Name: user_accounts uc_user_accounts_person; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_accounts
    ADD CONSTRAINT uc_user_accounts_person UNIQUE (person_id);


--
-- Name: user_accounts uc_user_accounts_provider_subject; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_accounts
    ADD CONSTRAINT uc_user_accounts_provider_subject UNIQUE (provider, provider_subject);


--
-- Name: user_accounts uc_user_accounts_username; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_accounts
    ADD CONSTRAINT uc_user_accounts_username UNIQUE (username);



--
-- Name: idx_appointments_patient_time; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_appointments_patient_time ON public.appointments USING btree (patient_id, start_time, end_time);


--
-- Name: idx_appointments_practitioner_time; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_appointments_practitioner_time ON public.appointments USING btree (practitioner_id, start_time, end_time);


--
-- Name: appointments fk_appointments_on_department; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.appointments
    ADD CONSTRAINT fk_appointments_on_department FOREIGN KEY (department_id) REFERENCES public.departments(department_id);


--
-- Name: appointments fk_appointments_on_patient; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.appointments
    ADD CONSTRAINT fk_appointments_on_patient FOREIGN KEY (patient_id) REFERENCES public.patients(patient_id);


--
-- Name: appointments fk_appointments_on_practitioner; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.appointments
    ADD CONSTRAINT fk_appointments_on_practitioner FOREIGN KEY (practitioner_id) REFERENCES public.practitioners(practitioner_id);


--
-- Name: department_practitioners fk_deppra_on_department; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.department_practitioners
    ADD CONSTRAINT fk_deppra_on_department FOREIGN KEY (department_id) REFERENCES public.departments(department_id);


--
-- Name: department_practitioners fk_deppra_on_practitioner; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.department_practitioners
    ADD CONSTRAINT fk_deppra_on_practitioner FOREIGN KEY (practitioner_id) REFERENCES public.practitioners(practitioner_id);


--
-- Name: patients fk_patients_on_person; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.patients
    ADD CONSTRAINT fk_patients_on_person FOREIGN KEY (patient_id) REFERENCES public.persons(person_id) ON DELETE CASCADE;


--
-- Name: practitioner_specialties fk_practitioner_specialties_on_practitioner; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.practitioner_specialties
    ADD CONSTRAINT fk_practitioner_specialties_on_practitioner FOREIGN KEY (practitioner_id) REFERENCES public.practitioners(practitioner_id);


--
-- Name: practitioners fk_practitioners_on_person; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.practitioners
    ADD CONSTRAINT fk_practitioners_on_person FOREIGN KEY (practitioner_id) REFERENCES public.persons(person_id) ON DELETE CASCADE;


--
-- Name: user_accounts fk_user_accounts_person; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_accounts
    ADD CONSTRAINT fk_user_accounts_person FOREIGN KEY (person_id) REFERENCES public.persons(person_id);


--
-- Name: user_roles fk_user_roles_user; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES public.user_accounts(user_id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

