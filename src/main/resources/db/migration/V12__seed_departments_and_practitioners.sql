-- ============================================================================
-- V12: Seed Realistic Departments, Practitioners, and Administrative Users
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. Seed Core Departments
-- ----------------------------------------------------------------------------
INSERT INTO departments (department_id, name, description, created_at, updated_at)
VALUES
    ('d1000000-0000-0000-0000-000000000001', 'Cardiology', 'Heart and cardiovascular health', NOW(), NOW()),
    ('d1000000-0000-0000-0000-000000000002', 'Dermatology', 'Skin, hair, and nail conditions', NOW(), NOW()),
    ('d1000000-0000-0000-0000-000000000003', 'Neurology', 'Brain, spine, and nervous system disorders', NOW(), NOW()),
    ('d1000000-0000-0000-0000-000000000004', 'Orthopedics', 'Bones, joints, ligaments, and musculoskeletal care', NOW(), NOW()),
    ('d1000000-0000-0000-0000-000000000005', 'Pediatrics', 'Comprehensive medical care for infants, children, and adolescents', NOW(), NOW()),
    ('d1000000-0000-0000-0000-000000000006', 'General Medicine', 'Primary care, chronic disease management, and internal medicine', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- ----------------------------------------------------------------------------
-- 2. Seed Practitioners (Persons & Practitioners tables)
-- ----------------------------------------------------------------------------

-- Dr. Sarah Connor (Cardiology)
INSERT INTO persons (person_id, first_name, last_name, id_number, date_of_birth, gender, phone_number, emergency_contact, created_at, updated_at)
VALUES ('e1000000-0000-0000-0000-000000000001', 'Sarah', 'Connor', '1000000001', '1978-04-12', 'female', '+1 555 0101', 'John Connor (+1 555 0191)', NOW(), NOW())
ON CONFLICT (person_id) DO NOTHING;

INSERT INTO practitioners (practitioner_id)
VALUES ('e1000000-0000-0000-0000-000000000001')
ON CONFLICT (practitioner_id) DO NOTHING;

INSERT INTO practitioner_specialties (practitioner_id, specialty)
VALUES
    ('e1000000-0000-0000-0000-000000000001', 'Interventional Cardiology'),
    ('e1000000-0000-0000-0000-000000000001', 'Cardiovascular Disease')
ON CONFLICT (practitioner_id, specialty) DO NOTHING;

INSERT INTO department_practitioners (department_id, practitioner_id)
VALUES ('d1000000-0000-0000-0000-000000000001', 'e1000000-0000-0000-0000-000000000001')
ON CONFLICT (department_id, practitioner_id) DO NOTHING;

-- Dr. Leonard McCoy (Dermatology)
INSERT INTO persons (person_id, first_name, last_name, id_number, date_of_birth, gender, phone_number, emergency_contact, created_at, updated_at)
VALUES ('e1000000-0000-0000-0000-000000000002', 'Leonard', 'McCoy', '1000000002', '1981-08-20', 'male', '+1 555 0102', 'Jim Kirk (+1 555 0192)', NOW(), NOW())
ON CONFLICT (person_id) DO NOTHING;

INSERT INTO practitioners (practitioner_id)
VALUES ('e1000000-0000-0000-0000-000000000002')
ON CONFLICT (practitioner_id) DO NOTHING;

INSERT INTO practitioner_specialties (practitioner_id, specialty)
VALUES
    ('e1000000-0000-0000-0000-000000000002', 'Dermatology'),
    ('e1000000-0000-0000-0000-000000000002', 'Cosmetic Dermatology')
ON CONFLICT (practitioner_id, specialty) DO NOTHING;

INSERT INTO department_practitioners (department_id, practitioner_id)
VALUES ('d1000000-0000-0000-0000-000000000002', 'e1000000-0000-0000-0000-000000000002')
ON CONFLICT (department_id, practitioner_id) DO NOTHING;

-- Dr. Gregory House (Neurology & General Medicine)
INSERT INTO persons (person_id, first_name, last_name, id_number, date_of_birth, gender, phone_number, emergency_contact, created_at, updated_at)
VALUES ('e1000000-0000-0000-0000-000000000003', 'Gregory', 'House', '1000000003', '1975-06-11', 'male', '+1 555 0103', 'James Wilson (+1 555 0193)', NOW(), NOW())
ON CONFLICT (person_id) DO NOTHING;

INSERT INTO practitioners (practitioner_id)
VALUES ('e1000000-0000-0000-0000-000000000003')
ON CONFLICT (practitioner_id) DO NOTHING;

INSERT INTO practitioner_specialties (practitioner_id, specialty)
VALUES
    ('e1000000-0000-0000-0000-000000000003', 'Neurology'),
    ('e1000000-0000-0000-0000-000000000003', 'Diagnostic Medicine')
ON CONFLICT (practitioner_id, specialty) DO NOTHING;

INSERT INTO department_practitioners (department_id, practitioner_id)
VALUES
    ('d1000000-0000-0000-0000-000000000003', 'e1000000-0000-0000-0000-000000000003'),
    ('d1000000-0000-0000-0000-000000000006', 'e1000000-0000-0000-0000-000000000003')
ON CONFLICT (department_id, practitioner_id) DO NOTHING;

-- Dr. Stephen Strange (Orthopedics & Neurology)
INSERT INTO persons (person_id, first_name, last_name, id_number, date_of_birth, gender, phone_number, emergency_contact, created_at, updated_at)
VALUES ('e1000000-0000-0000-0000-000000000004', 'Stephen', 'Strange', '1000000004', '1982-11-18', 'male', '+1 555 0104', 'Christine Palmer (+1 555 0194)', NOW(), NOW())
ON CONFLICT (person_id) DO NOTHING;

INSERT INTO practitioners (practitioner_id)
VALUES ('e1000000-0000-0000-0000-000000000004')
ON CONFLICT (practitioner_id) DO NOTHING;

INSERT INTO practitioner_specialties (practitioner_id, specialty)
VALUES
    ('e1000000-0000-0000-0000-000000000004', 'Orthopedic Surgery'),
    ('e1000000-0000-0000-0000-000000000004', 'Spine Surgery')
ON CONFLICT (practitioner_id, specialty) DO NOTHING;

INSERT INTO department_practitioners (department_id, practitioner_id)
VALUES
    ('d1000000-0000-0000-0000-000000000004', 'e1000000-0000-0000-0000-000000000004'),
    ('d1000000-0000-0000-0000-000000000003', 'e1000000-0000-0000-0000-000000000004')
ON CONFLICT (department_id, practitioner_id) DO NOTHING;

-- Dr. John Dorian (Pediatrics)
INSERT INTO persons (person_id, first_name, last_name, id_number, date_of_birth, gender, phone_number, emergency_contact, created_at, updated_at)
VALUES ('e1000000-0000-0000-0000-000000000005', 'John', 'Dorian', '1000000005', '1984-03-25', 'male', '+1 555 0105', 'Christopher Turk (+1 555 0195)', NOW(), NOW())
ON CONFLICT (person_id) DO NOTHING;

INSERT INTO practitioners (practitioner_id)
VALUES ('e1000000-0000-0000-0000-000000000005')
ON CONFLICT (practitioner_id) DO NOTHING;

INSERT INTO practitioner_specialties (practitioner_id, specialty)
VALUES
    ('e1000000-0000-0000-0000-000000000005', 'Pediatrics'),
    ('e1000000-0000-0000-0000-000000000005', 'Pediatric Care')
ON CONFLICT (practitioner_id, specialty) DO NOTHING;

INSERT INTO department_practitioners (department_id, practitioner_id)
VALUES ('d1000000-0000-0000-0000-000000000005', 'e1000000-0000-0000-0000-000000000005')
ON CONFLICT (department_id, practitioner_id) DO NOTHING;

-- Dr. Meredith Grey (General Medicine & Pediatrics)
INSERT INTO persons (person_id, first_name, last_name, id_number, date_of_birth, gender, phone_number, emergency_contact, created_at, updated_at)
VALUES ('e1000000-0000-0000-0000-000000000006', 'Meredith', 'Grey', '1000000006', '1983-09-08', 'female', '+1 555 0106', 'Derek Shepherd (+1 555 0196)', NOW(), NOW())
ON CONFLICT (person_id) DO NOTHING;

INSERT INTO practitioners (practitioner_id)
VALUES ('e1000000-0000-0000-0000-000000000006')
ON CONFLICT (practitioner_id) DO NOTHING;

INSERT INTO practitioner_specialties (practitioner_id, specialty)
VALUES
    ('e1000000-0000-0000-0000-000000000006', 'General Surgery'),
    ('e1000000-0000-0000-0000-000000000006', 'Internal Medicine')
ON CONFLICT (practitioner_id, specialty) DO NOTHING;

INSERT INTO department_practitioners (department_id, practitioner_id)
VALUES
    ('d1000000-0000-0000-0000-000000000006', 'e1000000-0000-0000-0000-000000000006'),
    ('d1000000-0000-0000-0000-000000000005', 'e1000000-0000-0000-0000-000000000006')
ON CONFLICT (department_id, practitioner_id) DO NOTHING;

-- ----------------------------------------------------------------------------
-- 3. Seed User Accounts & Roles
-- Default password for all seeded practitioners: 'practitioner123'
-- Hash: $2a$10$U4wTfeOZLPreV7NuUmDJ9OAYZBZcg/YvUnuvkcnXDqhnkf8bHpuHS
-- ----------------------------------------------------------------------------

-- Seed Practitioner User Accounts
INSERT INTO user_accounts (user_id, provider, provider_subject, display_name, username, email, password_hash, person_id, created_at, updated_at)
VALUES
    ('a1000000-0000-0000-0000-000000000001', 'local', 'sarah.connor', 'Dr. Sarah Connor', 'sarah.connor', 'sarah.connor@evergreen.com', '$2a$10$U4wTfeOZLPreV7NuUmDJ9OAYZBZcg/YvUnuvkcnXDqhnkf8bHpuHS', 'e1000000-0000-0000-0000-000000000001', NOW(), NOW()),
    ('a1000000-0000-0000-0000-000000000002', 'local', 'leonard.mccoy', 'Dr. Leonard McCoy', 'leonard.mccoy', 'leonard.mccoy@evergreen.com', '$2a$10$U4wTfeOZLPreV7NuUmDJ9OAYZBZcg/YvUnuvkcnXDqhnkf8bHpuHS', 'e1000000-0000-0000-0000-000000000002', NOW(), NOW()),
    ('a1000000-0000-0000-0000-000000000003', 'local', 'gregory.house', 'Dr. Gregory House', 'gregory.house', 'gregory.house@evergreen.com', '$2a$10$U4wTfeOZLPreV7NuUmDJ9OAYZBZcg/YvUnuvkcnXDqhnkf8bHpuHS', 'e1000000-0000-0000-0000-000000000003', NOW(), NOW()),
    ('a1000000-0000-0000-0000-000000000004', 'local', 'stephen.strange', 'Dr. Stephen Strange', 'stephen.strange', 'stephen.strange@evergreen.com', '$2a$10$U4wTfeOZLPreV7NuUmDJ9OAYZBZcg/YvUnuvkcnXDqhnkf8bHpuHS', 'e1000000-0000-0000-0000-000000000004', NOW(), NOW()),
    ('a1000000-0000-0000-0000-000000000005', 'local', 'john.dorian', 'Dr. John Dorian', 'john.dorian', 'john.dorian@evergreen.com', '$2a$10$U4wTfeOZLPreV7NuUmDJ9OAYZBZcg/YvUnuvkcnXDqhnkf8bHpuHS', 'e1000000-0000-0000-0000-000000000005', NOW(), NOW()),
    ('a1000000-0000-0000-0000-000000000006', 'local', 'meredith.grey', 'Dr. Meredith Grey', 'meredith.grey', 'meredith.grey@evergreen.com', '$2a$10$U4wTfeOZLPreV7NuUmDJ9OAYZBZcg/YvUnuvkcnXDqhnkf8bHpuHS', 'e1000000-0000-0000-0000-000000000006', NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role)
VALUES
    ('a1000000-0000-0000-0000-000000000001', 'ROLE_PRACTITIONER'),
    ('a1000000-0000-0000-0000-000000000002', 'ROLE_PRACTITIONER'),
    ('a1000000-0000-0000-0000-000000000003', 'ROLE_PRACTITIONER'),
    ('a1000000-0000-0000-0000-000000000004', 'ROLE_PRACTITIONER'),
    ('a1000000-0000-0000-0000-000000000005', 'ROLE_PRACTITIONER'),
    ('a1000000-0000-0000-0000-000000000006', 'ROLE_PRACTITIONER')
ON CONFLICT (user_id, role) DO NOTHING;

-- ----------------------------------------------------------------------------
-- 4. Seed Initial Administrative User Account
-- Default password: 'admin123'
-- Hash: $2a$10$EvliguUJKDpywhdQSk3OiOICIlYmnFOCJsS5x14/50rJUV/7ifDd2
-- ----------------------------------------------------------------------------
INSERT INTO user_accounts (user_id, provider, provider_subject, display_name, username, email, password_hash, person_id, created_at, updated_at)
VALUES (
    'a1000000-0000-0000-0000-000000000099',
    'local',
    'admin',
    'Administrator',
    'admin',
    'admin@evergreen.com',
    '$2a$10$EvliguUJKDpywhdQSk3OiOICIlYmnFOCJsS5x14/50rJUV/7ifDd2',
    NULL,
    NOW(),
    NOW()
)
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role)
VALUES ('a1000000-0000-0000-0000-000000000099', 'ROLE_ADMIN')
ON CONFLICT (user_id, role) DO NOTHING;
