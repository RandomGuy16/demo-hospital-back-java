-- Guest bookings can create a patient shell identified only by national idNumber,
-- contact details, and address. Full demographics are filled in at registration.
ALTER TABLE patients ALTER COLUMN first_name DROP NOT NULL;
ALTER TABLE patients ALTER COLUMN last_name DROP NOT NULL;
ALTER TABLE patients ALTER COLUMN date_of_birth DROP NOT NULL;