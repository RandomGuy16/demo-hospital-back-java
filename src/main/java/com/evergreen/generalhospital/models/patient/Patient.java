package com.evergreen.generalhospital.models.patient;

import com.evergreen.generalhospital.models.Person;
import com.evergreen.generalhospital.models.appointment.Appointment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

@Schema(name = "Patient", description = "Patient record")
@Entity
@Table(name = "patients")
@AttributeOverride(name = "firstName", column = @Column(name = "first_name", nullable = true))
@AttributeOverride(name = "lastName", column = @Column(name = "last_name", nullable = true))
@AttributeOverride(name = "dateOfBirth", column = @Column(name = "date_of_birth", nullable = true))
public class Patient extends Person {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "patient_id")
    private UUID patientId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "ShokoIeiri-1234567890ABCDEF12")
    @Column(nullable = false, unique = true, length = 50)
    private String mrn;  // Medical Record Number

    @Schema(example = "123 Main St, Springfield")
    @Column(nullable = false, length = 200)
    private String address;

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Appointment> appointments;

    // Constructors
    public Patient() {
        super();
    }

    public Patient(
        String firstName,
        String lastName,
        String idNumber,
        LocalDate dateOfBirth,
        String gender,
        String phoneNumber,
        String contacts,
        String mrn,
        String address) {
        super(firstName, lastName, idNumber, dateOfBirth, gender, phoneNumber, contacts);
        this.mrn = mrn;
        this.address = address;
    }

    // Getters and Setters
    public UUID getPatientId() {
        return patientId;
    }

    public String getMrn() {
        return mrn;
    }

    public void setMrn(String mrn) {
        this.mrn = mrn;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public void addAppointment(Appointment appointment) {
        if (!this.appointments.contains(appointment)) {
            this.appointments.add(appointment);
            appointment.setPatient(this);
        }
    }
}
