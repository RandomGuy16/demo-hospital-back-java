package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Schema(name = "Practitioner", description = "Practitioner record")
@Entity
@Table(name = "practitioners")
public class Practitioner extends Person {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "d2719c5d-84d1-43f6-a713-eef8a694be75")
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "practitioner_id")
    private UUID practitionerId;

    @Schema(example = "[\"Cardiology\", \"Internal Medicine\"]")
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
    @JsonIgnore
    @Schema(hidden = true)
    private List<Department> departments = new ArrayList<>();

    // Constructors
    public Practitioner() {
        super();
    }

    public Practitioner(String firstName, String lastName, String idNumber, LocalDate dateOfBirth,
                        String gender, String phoneNumber, String contacts) {
        super(firstName, lastName, idNumber, dateOfBirth, gender, phoneNumber, contacts);
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
