package com.evergreen.generalhospital.testsupport.util;

import com.evergreen.generalhospital.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class DatabaseCleanup {

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
    protected PersonRepository personRepository;

    /**
     * Deletes test data in dependency order so foreign keys do not block cleanup.
     */
    public void cleanDatabase() {
        appointmentRepository.deleteAll();
        userAccountRepository.deleteAll();
        departmentRepository.deleteAll();
        practitionerRepository.deleteAll();
        patientRepository.deleteAll();
        personRepository.deleteAll();
    }
}
