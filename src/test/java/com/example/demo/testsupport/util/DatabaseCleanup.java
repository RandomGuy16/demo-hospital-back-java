package com.example.demo.testsupport.util;

import com.example.demo.repositories.*;
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


    /**
     * Deletes test data in dependency order so foreign keys do not block cleanup.
     */
    public void cleanDatabase() {
        appointmentRepository.deleteAll();
        userAccountRepository.deleteAll();
        practitionerRepository.deleteAll();
        departmentRepository.deleteAll();
        patientRepository.deleteAll();
    }
}
