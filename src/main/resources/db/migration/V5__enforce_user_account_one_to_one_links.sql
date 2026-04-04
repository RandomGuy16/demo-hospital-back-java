ALTER TABLE user_accounts
    ADD CONSTRAINT uc_user_accounts_practitioner UNIQUE (practitioner_id);

ALTER TABLE user_accounts
    ADD CONSTRAINT uc_user_accounts_patient UNIQUE (patient_id);
