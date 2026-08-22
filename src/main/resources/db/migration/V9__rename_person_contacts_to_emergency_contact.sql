ALTER TABLE persons RENAME COLUMN contacts TO emergency_contact;
ALTER TABLE patients DROP COLUMN IF EXISTS emergency_contact;
