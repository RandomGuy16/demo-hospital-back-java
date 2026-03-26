package com.example.demo.models.useraccount;

import com.example.demo.models.patient.Patient;
import com.example.demo.models.practitioner.Practitioner;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Schema(name = "User", description = "Registered users record")
@Entity
@ValidUserAccountRoleLink
@Table(
    name = "user_accounts",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"provider", "provider_subject"}),
        @UniqueConstraint(columnNames = {"username"}),
        @UniqueConstraint(columnNames = {"email"})
    }
)
public class UserAccount {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID id;

    @Schema(example = "d2719c5d-84d1-43f6-a713-eef8a694be75")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practitioner_id", referencedColumnName = "practitioner_id", nullable = true)
    private Practitioner practitioner;

    @Schema(example = "d2719c5d-84d1-43f6-a713-eef8a694be75")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", nullable = true)
    private Patient patient;

    @Schema(example = "keycloak")
    @Column(nullable = false, length = 50)
    private String provider;

    @Schema(example = "1234567890")
    @Column(name = "provider_subject", nullable = false, length = 100)
    private String providerSubject;

    @Schema(example = "ROLE_ADMIN")
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Schema(example = "Doe")
    @Column(nullable = false, length = 50)
    private String displayName;

    @Schema(example = "jane.doe")
    @Column(nullable = false, length = 50)
    private String username;

    @Schema(example = "bat.man@example.com")
    @Column(length = 200)
    private String email;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "2026-03-17T12:30:00")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "2026-03-17T12:30:00")
    @Column(name = "updated_at", nullable = false, updatable = true)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UserAccount() {}
    public UserAccount(Practitioner practitioner, Patient patient, String provider,
                       String providerSubject, Role role, String displayName, String username, String email) {
        this.practitioner = practitioner;
        this.patient = patient;
        this.provider = provider;
        this.providerSubject = providerSubject;
        this.role = role;
        this.displayName = displayName;
        this.username = username;
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Practitioner getPractitioner() {
        return practitioner;
    }

    public void setPractitioner(Practitioner practitioner) {
        this.practitioner = practitioner;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderSubject() {
        return providerSubject;
    }

    public void setProviderSubject(String providerSubject) {
        this.providerSubject = providerSubject;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
