CREATE TABLE user_accounts
(
    user_id          UUID                        NOT NULL,
    practitioner_id  UUID,
    patient_id       UUID,
    provider         VARCHAR(50)                 NOT NULL,
    provider_subject VARCHAR(100)                NOT NULL,
    role             VARCHAR(50)                 NOT NULL,
    display_name     VARCHAR(50)                 NOT NULL,
    username         VARCHAR(50)                 NOT NULL,
    email            VARCHAR(200)                NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_user_accounts PRIMARY KEY (user_id),
    CONSTRAINT uc_user_accounts_provider_subject UNIQUE (provider, provider_subject),
    CONSTRAINT uc_user_accounts_username UNIQUE (username),
    CONSTRAINT uc_user_accounts_email UNIQUE (email),
    CONSTRAINT fk_user_accounts_practitioner FOREIGN KEY (practitioner_id) REFERENCES practitioners (practitioner_id),
    CONSTRAINT fk_user_accounts_patient FOREIGN KEY (patient_id) REFERENCES patients (patient_id),
    CONSTRAINT chk_user_accounts_role CHECK (role IN ('ROLE_ADMIN', 'ROLE_RECEPTIONIST', 'ROLE_PATIENT', 'ROLE_PRACTITIONER')),
    CONSTRAINT chk_user_accounts_role_link CHECK (
        (role = 'ROLE_PATIENT' AND patient_id IS NOT NULL AND practitioner_id IS NULL)
        OR (role = 'ROLE_PRACTITIONER' AND practitioner_id IS NOT NULL AND patient_id IS NULL)
        OR (role IN ('ROLE_ADMIN', 'ROLE_RECEPTIONIST') AND patient_id IS NULL AND practitioner_id IS NULL)
    )
);

ALTER TABLE appointments
    ADD COLUMN created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now();

ALTER TABLE appointments
    ADD COLUMN updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now();
