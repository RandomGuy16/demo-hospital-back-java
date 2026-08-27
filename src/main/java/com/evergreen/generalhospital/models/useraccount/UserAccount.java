package com.evergreen.generalhospital.models.useraccount;

import com.evergreen.generalhospital.models.patient.Patient;
import com.evergreen.generalhospital.models.practitioner.Practitioner;
import com.evergreen.generalhospital.models.Person;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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
    @JoinColumn(name = "person_id", referencedColumnName = "person_id", unique = true)
    private Person person;

    @Schema(example = "keycloak")
    @Column(nullable = false, length = 50)
    private String provider;

    @Schema(example = "1234567890")
    @Column(name = "provider_subject", nullable = false, length = 100)
    private String providerSubject;

    // by doing this, JPA automatically creates an intermediate table
    // with a composite key (user_id, role)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_roles",  // this creates the table
        joinColumns = @JoinColumn(name="user_id", referencedColumnName = "user_id")  // this maps user_id there
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)  // this creates the role field there
    private Set<Role> roles = new HashSet<>();

    @Schema(example = "Doe")
    @Column(nullable = false, length = 50)
    private String displayName;

    @Schema(example = "jane.doe")
    @Column(nullable = false, length = 50)
    private String username;

    @Schema(example = "bat.man@example.com")
    @Column(nullable = false, length = 200)
    private String email;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "2026-03-17T12:30:00")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "2026-03-17T12:30:00")
    @Column(name = "updated_at", nullable = false, updatable = true)
    private LocalDateTime updatedAt;

    @Schema(example = "a;sjghlioauheofjiahlkj")
    @Column(name = "password_hash", length = 1000)
    private String password;

    /**
     * Sets timestamps right before the row is inserted.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Refreshes the update timestamp right before the row is updated.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UserAccount() {}

    /**
     * Creates a fully populated user-account aggregate.
     *
     * @param practitioner optional practitioner link.
     * @param patient optional patient link.
     * @param provider external or local auth provider name.
     * @param providerSubject stable subject within that provider.
     * @param role application role.
     * @param displayName user-facing display name.
     * @param username unique application username.
     * @param email unique login email.
     * @param password encoded password hash.
     */
    public UserAccount(Practitioner practitioner, Patient patient, String provider, String providerSubject,
                       Role role, String displayName, String username, String email, String password) {
        this.practitioner = practitioner;
        this.patient = patient;
        this.provider = provider;
        this.providerSubject = providerSubject;
        this.role = role;
        this.displayName = displayName;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
