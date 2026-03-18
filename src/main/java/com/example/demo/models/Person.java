package com.example.demo.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Common person attributes shared by patients and practitioners")
@MappedSuperclass
public abstract class Person {
    @Schema(example = "Jane")
    @Column(nullable = false, length = 50)
    private String firstName;

    @Schema(example = "Doe")
    @Column(nullable = false, length = 50)
    private String lastName;

    @Schema(example = "1234567890")
    @Column(name = "id_number", nullable = false, length = 10)
    private String idNumber;

    @Schema(example = "1995-04-18")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Schema(example = "female")
    @Column(length = 20)
    private String gender;

    @Schema(example = "+1 555 0100")
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Schema(example = "jane.doe@example.com")
    @Column(length = 200)
    private String contacts;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "2026-03-17T12:30:00")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "2026-03-17T12:45:00")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // JPA requires a no-arg constructor
    protected Person() {
    }

    protected Person(String firstName, String lastName, String idNumber, LocalDate dateOfBirth,
                     String gender, String phoneNumber, String contacts) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.idNumber = idNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.contacts = contacts;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getContacts() {
        return contacts;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }
}
