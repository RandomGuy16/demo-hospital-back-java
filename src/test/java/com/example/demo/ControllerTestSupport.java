package com.example.demo;

import com.example.demo.models.Appointment;
import com.example.demo.models.Department;
import com.example.demo.models.Patient;
import com.example.demo.models.Practitioner;
import com.example.demo.repositories.AppointmentRepository;
import com.example.demo.repositories.DepartmentRepository;
import com.example.demo.repositories.PatientRepository;
import com.example.demo.repositories.PractitionerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
abstract class ControllerTestSupport {

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

    @BeforeEach
    void cleanDatabase() {
        appointmentRepository.deleteAll();
        practitionerRepository.deleteAll();
        departmentRepository.deleteAll();
        patientRepository.deleteAll();
    }

    protected String json(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
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
                specialties);
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
                firstName.toLowerCase() + "@example.com");
        practitioner.setSpecialties(specialties);
        return practitionerRepository.save(practitioner);
    }

    protected Department saveDepartment(String name, String description) {
        return departmentRepository.save(new Department(name, description));
    }

    protected Appointment saveAppointment(UUID patientId, UUID practitionerId, UUID departmentId, String status) {
        LocalDateTime start = LocalDateTime.now().plusDays(5);
        Appointment appointment = new Appointment(
                patientId,
                practitionerId,
                departmentId,
                start,
                start.plusMinutes(30),
                status);
        return appointmentRepository.save(appointment);
    }
}
