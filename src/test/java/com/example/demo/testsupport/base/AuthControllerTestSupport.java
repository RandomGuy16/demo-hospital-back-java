package com.example.demo.testsupport.base;

import com.example.demo.models.useraccount.UserAccount;
import com.example.demo.testsupport.fixtures.DomainFixtures;
import com.example.demo.testsupport.fixtures.AuthFixtures;
import com.example.demo.testsupport.util.DatabaseCleanup;
import com.example.demo.testsupport.util.JsonTestHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AuthControllerTestSupport {

    protected record TestSubjects(
            com.example.demo.models.patient.Patient patient,
            com.example.demo.models.practitioner.Practitioner practitioner,
            com.example.demo.models.department.Department department
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
    protected DomainFixtures domainFixtures;

    @Autowired
    protected AuthFixtures authFixtures;

    @Autowired
    protected JsonTestHelper jsonTestHelper;

    protected TestSubjects defaultSubjects;
    protected TestSubjects funnySubjects;

    /**
     * Resets the database and seeds the default domain fixtures before each auth test.
     */
    @BeforeEach
    void setUp() {
        databaseCleanup.cleanDatabase();
        domainFixtures.seedDefaultSubjects();
        domainFixtures.seedFunnySubjects();
        defaultSubjects = new TestSubjects(
                domainFixtures.defaultSubjects.patient(),
                domainFixtures.defaultSubjects.practitioner(),
                domainFixtures.defaultSubjects.department()
        );
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
     * Seeds the standard auth fixtures and returns them in the base-test shape.
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
}
