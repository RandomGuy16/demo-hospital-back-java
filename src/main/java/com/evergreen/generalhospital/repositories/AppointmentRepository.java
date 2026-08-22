package com.evergreen.generalhospital.repositories;

import com.evergreen.generalhospital.models.appointment.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    @Override
    List<Appointment> findAll();

    List<Appointment> findByPractitioner_Id(UUID practitionerId);

    List<Appointment> findByPatient_Id(UUID patientId);

    List<Appointment> findByDepartment_DepartmentId(UUID departmentId);

    Set<Appointment> findAppointmentsByStartAfter(LocalDateTime startTime);
    Set<Appointment> findAppointmentsByEndAfter(LocalDateTime startTime);
    Set<Appointment> findAppointmentsByStartBefore(LocalDateTime endTime);
    Set<Appointment> findAppointmentsByEndBefore(LocalDateTime endTime);

    // methods to find appointment collisions for patients and practitioners
    Set<Appointment> findByPatient_IdAndStartBeforeAndEndAfter(UUID patientId, LocalDateTime requestedEnd, LocalDateTime requestedStart);
    boolean existsByPatient_IdAndStartBeforeAndEndAfter(UUID patientId, LocalDateTime requestedEnd, LocalDateTime requestedStart);
    boolean existsByPatient_IdAndAppointmentIdNotAndStartBeforeAndEndAfter(
            UUID patientId,
            UUID appointmentId,
            LocalDateTime requestedEnd,
            LocalDateTime requestedStart
    );

    Set<Appointment> findByPractitioner_IdAndStartBeforeAndEndAfter(UUID practitionerId, LocalDateTime requestedEnd, LocalDateTime requestedStart);
    boolean existsByPractitioner_IdAndStartBeforeAndEndAfter(UUID practitionerId, LocalDateTime requestedEnd, LocalDateTime requestedStart);
    boolean existsByPractitioner_IdAndAppointmentIdNotAndStartBeforeAndEndAfter(
            UUID practitionerId,
            UUID appointmentId,
            LocalDateTime requestedEnd,
            LocalDateTime requestedStart
    );

}
