package com.evergreen.generalhospital.testsupport.fixtures;

import com.evergreen.generalhospital.models.department.Department;
import com.evergreen.generalhospital.models.patient.Patient;
import com.evergreen.generalhospital.models.practitioner.Practitioner;
import com.evergreen.generalhospital.testsupport.factories.DepartmentFactory;
import com.evergreen.generalhospital.testsupport.factories.PatientFactory;
import com.evergreen.generalhospital.testsupport.factories.PractitionerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@TestComponent
public class DomainFixtures {

    @Autowired
    private PatientFactory patientFactory;

    @Autowired
    private PractitionerFactory practitionerFactory;

    @Autowired
    private DepartmentFactory departmentFactory;

    public record TestSubjects(
        Patient patient,
        Practitioner practitioner,
        Department department
    ) {
    }

    public TestSubjects defaultSubjects;
    public TestSubjects funnySubjects;

    /**
     * Seeds the default domain trio used by most controller tests.
     */
    public void seedDefaultSubjects() {
        Patient patient = patientFactory.savePatient(
            "John",
            "Doe",
            "1234567890",
            "male",
            LocalDate.of(1995, 4, 18)
        );
        Department department = departmentFactory.saveDepartment("Cardiology", "Handles heart care");
        Practitioner practitioner = practitionerFactory.savePractitioner(
            "Shoko",
            "Ieiri",
            "7482736581",
            "female",
            LocalDate.of(1992, 6, 12),
            new ArrayList<>(List.of("Cardiology", "Sorcery"))
        );
        defaultSubjects = new TestSubjects(patient, practitioner, department);
    }

    /**
     * Seeds a second domain trio with distinct values for sort and update scenarios.
     */
    public void seedFunnySubjects() {
        Patient patient = patientFactory.savePatient(
            "Anita",
            "Bath",
            "9000000001",
            "female",
            LocalDate.of(1988, 2, 29)
        );

        Department department = departmentFactory.saveDepartment(
            "Duckology",
            "Specializes in duck-related emergencies and suspicious squeaking"
        );

        Practitioner practitioner = practitionerFactory.savePractitioner(
            "Holly",
            "Day",
            "9000000002",
            "nonbinary",
            LocalDate.of(1990, 10, 31),
            new ArrayList<>(List.of("Chaos Management", "Duck Whispering"))
        );
        funnySubjects = new TestSubjects(patient, practitioner, department);
    }

}
