-- intermediate table for @ElementCollection
CREATE TABLE user_roles
(
    user_id UUID        NOT NULL,
    role    VARCHAR(50) NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES user_accounts (user_id) ON DELETE CASCADE,
    CONSTRAINT chk_user_roles_role CHECK (role IN ('ROLE_ADMIN', 'ROLE_RECEPTIONIST', 'ROLE_PATIENT', 'ROLE_PRACTITIONER'))
);

-- backfill existing roles from user_accounts into user_roles
INSERT INTO user_roles (user_id, role)
SELECT user_id, role
FROM user_accounts
WHERE role IS NOT NULL;

-- drop obsolete role constraints and the single role column from user_accounts
ALTER TABLE user_accounts
    DROP CONSTRAINT IF EXISTS chk_user_accounts_role_link;

ALTER TABLE user_accounts
    DROP CONSTRAINT IF EXISTS chk_user_accounts_role;

ALTER TABLE user_accounts
    DROP COLUMN IF EXISTS role;

-- add person_id to link UserAccount directly to Person
ALTER TABLE user_accounts
    ADD COLUMN person_id UUID;

-- backfill person_id from existing patient_id or practitioner_id
UPDATE user_accounts
SET person_id = COALESCE(patient_id, practitioner_id);

ALTER TABLE user_accounts
    ADD CONSTRAINT fk_user_accounts_person FOREIGN KEY (person_id) REFERENCES persons (person_id),
    ADD CONSTRAINT uc_user_accounts_person UNIQUE (person_id);

-- drop obsolete columns and constraints
ALTER TABLE user_accounts
    DROP CONSTRAINT IF EXISTS uc_user_accounts_practitioner,
    DROP CONSTRAINT IF EXISTS uc_user_accounts_patient,
    DROP CONSTRAINT IF EXISTS fk_user_accounts_practitioner,
    DROP CONSTRAINT IF EXISTS fk_user_accounts_patient,
    DROP COLUMN IF EXISTS practitioner_id,
    DROP COLUMN IF EXISTS patient_id;
