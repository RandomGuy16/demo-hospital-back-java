package com.example.demo.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "practitioners")
public class Practitioner extends Person {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "practitioner_id")
    private UUID practitionerId;

    @ElementCollection
    @CollectionTable(name = "practitioner_specialties", 
                     joinColumns = @JoinColumn(name = "practitioner_id"))
    @Column(name = "specialty", length = 100)
    private List<String> specialties = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "practitioner_departments",
        joinColumns = @JoinColumn(name = "practitioner_id"),
        inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    private List<Department> departments = new ArrayList<>();

    // Constructors
    public Practitioner() {
        super();
    }

    public Practitioner(String firstName, String lastName, LocalDate dateOfBirth,
                        String gender, String phoneNumber, String contacts) {
        super(firstName, lastName, dateOfBirth, gender, phoneNumber, contacts);
    }

    // Getters and Setters
    public UUID getPractitionerId() {
        return practitionerId;
    }

    public List<String> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<String> specialties) {
        this.specialties = specialties;
    }

    public void addSpecialty(String specialty) {
        if (!this.specialties.contains(specialty)) {
            this.specialties.add(specialty);
        }
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public void addDepartment(Department department) {
        if (!this.departments.contains(department)) {
            this.departments.add(department);
        }
    }
}
