package com.example.demo;

import com.example.demo.models.appointment.Appointment;
import com.example.demo.models.appointment.AppointmentStatus;
import com.example.demo.models.department.Department;
import com.example.demo.models.patient.Patient;
import com.example.demo.models.practitioner.Practitioner;
import com.example.demo.models.useraccount.Role;
import com.example.demo.models.useraccount.UserAccount;
import com.example.demo.repositories.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
abstract class ControllerTestSupport {

    protected record TestSubjects(
            Patient patient,
            Practitioner practitioner,
            Department department
    ) {
    }
    
    protected record TestUserSubjects(
        UserAccount admin,
        UserAccount receptionist
    ) {}

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected PatientRepository patientRepository;

    @Autowired
    protected PractitionerRepository practitionerRepository;

    @Autowired
    protected DepartmentRepository departmentRepository;

    @Autowired
    protected AppointmentRepository appointmentRepository;
    
    @Autowired
    protected UserAccountRepository userAccountRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;
    
    protected TestSubjects defaultSubjects;
    protected TestSubjects funnySubjects;

    @BeforeEach
    void setUp() {
        cleanDatabase();
        defaultSubjects = seedDefaultSubjects();
        funnySubjects = seedFunnySubjects();
    }

    protected void cleanDatabase() {
        appointmentRepository.deleteAll();
        userAccountRepository.deleteAll();
        practitionerRepository.deleteAll();
        departmentRepository.deleteAll();
        patientRepository.deleteAll();
    }

    protected TestSubjects seedDefaultSubjects() {
        Patient patient = savePatient(
            "John",
            "Doe",
            "1234567890",
            "male",
            LocalDate.of(1995, 4, 18)
        );
        Department department = saveDepartment("Cardiology", "Handles heart care");
        Practitioner practitioner = savePractitioner(
                "Shoko",
                "Ieiri",
                "7482736581",
                "female",
                LocalDate.of(1992, 6, 12),
                new ArrayList<>(List.of("Cardiology", "Sorcery"))
        );
        return new TestSubjects(patient, practitioner, department);
    }

    protected TestSubjects seedFunnySubjects() {
        Patient patient = savePatient(
                "Anita",
                "Bath",
                "9000000001",
                "female",
                LocalDate.of(1988, 2, 29)
        );

        Department department = saveDepartment(
                "Duckology",
                "Specializes in duck-related emergencies and suspicious squeaking"
        );

        Practitioner practitioner = savePractitioner(
                "Holly",
                "Day",
                "9000000002",
                "nonbinary",
                LocalDate.of(1990, 10, 31),
                new ArrayList<>(List.of("Chaos Management", "Duck Whispering"))
        );
        return new TestSubjects(patient, practitioner, department);
    }
    
    protected TestUserSubjects seedUserSubjects() {
        UserAccount admin = saveUserAccount(
                "Admin User",
                "admin",
                null,
                null,
            "admin-subject",
                Role.ROLE_ADMIN,
                "admin@example.com",
                "admin-password"
        );

        UserAccount receptionist = saveUserAccount(
                "Receptionist User",
                "receptionist",
                null,
                null,
            "receptionist-subject",
                Role.ROLE_RECEPTIONIST,
                "receptionist@example.com",
                "receptionist-password"
        );

        return new TestUserSubjects(admin, receptionist);
    }

    protected String json(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }

    protected Patient savePatient(String firstName, String lastName, String idNumber) {
        return savePatient(
                firstName,
                lastName,
                idNumber,
                "female",
                LocalDate.of(1992, 1, 10)
        );
    }

    protected Patient savePatient(String firstName,
                                  String lastName,
                                  String idNumber,
                                  String gender,
                                  LocalDate dateOfBirth) {

        String normalizedGender = (gender == null || gender.isBlank())
                ? "female"
                : gender;

        LocalDate normalizedDob = (dateOfBirth != null)
                ? dateOfBirth
                : LocalDate.of(1992, 1, 10);

        Patient patient = new Patient(
                firstName,
                lastName,
                idNumber,
                normalizedDob,
                normalizedGender,
                "+1 555 0100",
                firstName.toLowerCase() + "@example.com",
                "MRN-" + idNumber,
                "123 Main St"
        );

        return patientRepository.save(patient);
    }

    protected Practitioner savePractitioner(String firstName,
                                            String lastName,
                                            String idNumber,
                                            List<String> specialties) {
        return savePractitioner(
                firstName,
                lastName,
                idNumber,
                "female",
                LocalDate.of(1992, 1, 10),
                specialties
        );
    }

    protected Practitioner savePractitioner(String firstName,
                                            String lastName,
                                            String idNumber,
                                            String gender,
                                            LocalDate dateOfBirth,
                                            List<String> specialties) {

        String normalizedGender = (gender == null || gender.isBlank())
                ? "female"
                : gender;

        LocalDate normalizedDob = (dateOfBirth != null)
                ? dateOfBirth
                : LocalDate.of(1992, 1, 10);

        Practitioner practitioner = new Practitioner(
                firstName,
                lastName,
                idNumber,
                normalizedDob,
                normalizedGender,
                "+1 555 0200",
                firstName.toLowerCase() + "@example.com"
        );
        practitioner.setSpecialties(specialties);
        return practitionerRepository.save(practitioner);
    }

    protected Department saveDepartment(String name, String description) {
        return departmentRepository.save(new Department(name, description));
    }

    protected Appointment saveAppointment(Patient patient, Practitioner practitioner, Department department, String status) {
        return saveAppointment(patient, practitioner, department, AppointmentStatus.valueOf(status));
    }

    protected Appointment saveAppointment(Patient patient,
                                          Practitioner practitioner,
                                          Department department,
                                          AppointmentStatus status) {
        LocalDateTime start = LocalDateTime.now().plusDays(5);
        Appointment appointment = new Appointment(
                patient,
                practitioner,
                department,
                start,
                start.plusMinutes(30),
                status
        );
        return appointmentRepository.save(appointment);
    }
    
    protected UserAccount saveUserAccount() {
        return saveUserAccount(
                "John Doe",
                "john.doe",
                null,
                null,
            "subject-" + System.nanoTime(),
                Role.ROLE_ADMIN,
                "john.doe@example.com",
                "strong-password"
        );
    }

    protected UserAccount saveUserAccount(String displayName,
                                          String username,
                                          UUID practitionerId,
                                          UUID patientId,
                                          String providerSubject,
                                          Role role,
                                          String email,
                                          String password) {

        return userAccountRepository.save(new UserAccount(
                practitionerId == null ? null : practitionerRepository.findById(practitionerId).orElse(null),
                patientId == null ? null : patientRepository.findById(patientId).orElse(null),
                "local",
                providerSubject,
                role,
                displayName,
                username,
                email,
                passwordEncoder.encode(password)
        ));
    }
}
