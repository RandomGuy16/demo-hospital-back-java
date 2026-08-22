package com.evergreen.generalhospital.testsupport.base;

import com.evergreen.generalhospital.models.appointment.Appointment;
import com.evergreen.generalhospital.models.appointment.AppointmentStatus;
import com.evergreen.generalhospital.models.department.Department;
import com.evergreen.generalhospital.models.patient.Patient;
import com.evergreen.generalhospital.models.practitioner.Practitioner;
import com.evergreen.generalhospital.models.useraccount.Role;
import com.evergreen.generalhospital.models.useraccount.UserAccount;
import com.evergreen.generalhospital.testsupport.factories.AppointmentFactory;
import com.evergreen.generalhospital.testsupport.factories.DepartmentFactory;
import com.evergreen.generalhospital.testsupport.factories.PatientFactory;
import com.evergreen.generalhospital.testsupport.factories.PractitionerFactory;
import com.evergreen.generalhospital.testsupport.factories.UserAccountFactory;
import com.evergreen.generalhospital.testsupport.fixtures.DomainFixtures;
import com.evergreen.generalhospital.testsupport.fixtures.AuthFixtures;
import com.evergreen.generalhospital.testsupport.util.DatabaseCleanup;
import com.evergreen.generalhospital.testsupport.util.JsonTestHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ComponentScan(basePackages = "com.evergreen.generalhospital.testsupport")
public abstract class CrudControllerTestSupport {

    protected record TestSubjects(
            Patient patient,
            Practitioner practitioner,
            Department department
    ) {
    }

    protected record TestUserSubjects(
            UserAccount admin,
            UserAccount receptionist
    ) {
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected DatabaseCleanup databaseCleanup;

    @Autowired
    protected JsonTestHelper jsonTestHelper;

    @Autowired
    protected DomainFixtures domainFixtures;

    @Autowired
    protected AuthFixtures authFixtures;

    @Autowired
    protected PatientFactory patientFactory;

    @Autowired
    protected PractitionerFactory practitionerFactory;

    @Autowired
    protected DepartmentFactory departmentFactory;

    @Autowired
    protected AppointmentFactory appointmentFactory;

    @Autowired
    protected UserAccountFactory userAccountFactory;

    protected TestSubjects defaultSubjects;
    protected TestSubjects funnySubjects;

    /**
     * Resets the database and seeds default CRUD fixtures before each test.
     */
    @BeforeEach
    void setUp() {
        databaseCleanup.cleanDatabase();
        seedDefaultSubjects();
        seedFunnySubjects();
    }

    /**
     * Resets the database after each test ends to guarantee clean state.
     */
    @AfterEach
    void tearDown() {
        databaseCleanup.cleanDatabase();
    }

    /**
     * Seeds default subjects (John Doe, Shoko Ieiri, Cardiology department).
     */
    protected void seedDefaultSubjects() {
        domainFixtures.seedDefaultSubjects();
        defaultSubjects = new TestSubjects(
                domainFixtures.defaultSubjects.patient(),
                domainFixtures.defaultSubjects.practitioner(),
                domainFixtures.defaultSubjects.department()
        );
    }

    /**
     * Seeds funny subjects (Anita Bath, Holly Day, Duckology department).
     */
    protected void seedFunnySubjects() {
        domainFixtures.seedFunnySubjects();
        funnySubjects = new TestSubjects(
                domainFixtures.funnySubjects.patient(),
                domainFixtures.funnySubjects.practitioner(),
                domainFixtures.funnySubjects.department()
        );
    }

    /**
     * Serializes an object to JSON for MockMvc requests.
     *
     * @param value object to serialize.
     * @return JSON representation of the object.
     * @throws JsonProcessingException if the object cannot be serialized.
     */
    protected String json(Object value) throws JsonProcessingException {
        return jsonTestHelper.json(value);
    }

    /**
     * Deletes all persisted test data in foreign-key-safe order.
     */
    protected void cleanDatabase() {
        databaseCleanup.cleanDatabase();
    }

    /**
     * Seeds the standard auth fixtures and returns them in the legacy support shape.
     *
     * @return seeded auth test subjects.
     */
    protected TestUserSubjects seedUserSubjects() {
        authFixtures.seedUserSubjects();
        return new TestUserSubjects(
                authFixtures.defaultUserSubjects.admin(),
                authFixtures.defaultUserSubjects.receptionist()
        );
    }

    protected Patient savePatient(String firstName, String lastName, String idNumber) {
        return patientFactory.savePatient(firstName, lastName, idNumber);
    }

    protected Patient savePatient(String firstName,
                                  String lastName,
                                  String idNumber,
                                  String gender,
                                  LocalDate dateOfBirth) {
        return patientFactory.savePatient(firstName, lastName, idNumber, gender, dateOfBirth);
    }

    protected Practitioner savePractitioner(String firstName,
                                            String lastName,
                                            String idNumber,
                                            List<String> specialties) {
        return practitionerFactory.savePractitioner(firstName, lastName, idNumber, specialties);
    }

    protected Practitioner savePractitioner(String firstName,
                                            String lastName,
                                            String idNumber,
                                            String gender,
                                            LocalDate dateOfBirth,
                                            List<String> specialties) {
        return practitionerFactory.savePractitioner(firstName, lastName, idNumber, gender, dateOfBirth, specialties);
    }

    protected Department saveDepartment(String name, String description) {
        return departmentFactory.saveDepartment(name, description);
    }

    protected Appointment saveAppointment(Patient patient, Practitioner practitioner, Department department, String status) {
        return appointmentFactory.saveAppointment(patient, practitioner, department, status);
    }

    protected Appointment saveAppointment(Patient patient,
                                          Practitioner practitioner,
                                          Department department,
                                          AppointmentStatus status) {
        return appointmentFactory.saveAppointment(patient, practitioner, department, status);
    }

    protected UserAccount saveUserAccount() {
        return userAccountFactory.saveUserAccount();
    }

    protected UserAccount saveUserAccount(String displayName,
                                          String username,
                                          UUID practitionerId,
                                          UUID patientId,
                                          String providerSubject,
                                          Role role,
                                          String email,
                                          String password) {
        return userAccountFactory.saveUserAccount(
                displayName,
                username,
                practitionerId,
                patientId,
                providerSubject,
                role,
                email,
                password
        );
    }
}
