package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Schema(name = "Department", description = "Hospital department")
@Entity
@Table(name = "departments")
public class Department {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "a0b1f54e-98c4-4e4d-9412-2eaf3e0c8695")
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "department_id")
    private UUID departmentId;

    @Schema(example = "Cardiology")
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Schema(example = "Handles heart and cardiovascular care")
    @Column(nullable = false, length = 200)
    private String description;

    @ManyToMany
    @JoinTable(
        name = "department_practitioners",
        joinColumns = @JoinColumn(name = "department_id"),
        inverseJoinColumns = @JoinColumn(name = "practitioner_id")
    )
    @JsonIgnore
    @Schema(hidden = true)
    private final List<Practitioner> practitioners = new ArrayList<>();

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "2026-03-17T12:30:00")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "2026-03-17T12:45:00")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Department() {

    }
    public Department(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Practitioner> getPractitioners() {
        return practitioners;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
