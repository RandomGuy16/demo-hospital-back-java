package com.evergreen.generalhospital.repositories;

import com.evergreen.generalhospital.models.appointment.Appointment;
import com.evergreen.generalhospital.models.appointment.AppointmentStatus;
import com.evergreen.generalhospital.models.patient.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    Page<Appointment> findByPatient_Id(UUID patientId, Pageable pageable);

    List<Appointment> findByPatient(Patient patient);

    List<Appointment> findByDepartment_DepartmentId(UUID departmentId);

    Set<Appointment> findAppointmentsByStartAfter(LocalDateTime startTime);
    Set<Appointment> findAppointmentsByEndAfter(LocalDateTime startTime);
    Set<Appointment> findAppointmentsByStartBefore(LocalDateTime endTime);
    Set<Appointment> findAppointmentsByEndBefore(LocalDateTime endTime);

    // methods to find appointment collisions for patients and practitioners
    Set<Appointment> findByPatient_IdAndStartBeforeAndEndAfter(UUID patientId, LocalDateTime requestedEnd, LocalDateTime requestedStart);
    boolean existsByPatient_IdAndStartBeforeAndEndAfter(
        UUID patientId,
        LocalDateTime requestedEnd,
        LocalDateTime requestedStart
    );
    boolean existsByPatient_IdAndAppointmentIdNotAndStartBeforeAndEndAfter(
            UUID patientId,
            UUID appointmentId,
            LocalDateTime requestedEnd,
            LocalDateTime requestedStart
    );

    Set<Appointment> findByPractitioner_IdAndStartBeforeAndEndAfter(UUID practitionerId, LocalDateTime requestedEnd, LocalDateTime requestedStart);

    // appointment that starts before and ends after, collision
    boolean existsByPractitioner_IdAndStartBeforeAndEndAfter(
        UUID practitionerId,
        LocalDateTime requestedEnd,
        LocalDateTime requestedStart
    );
    // only used in tests
    boolean existsByPractitioner_IdAndAppointmentIdNotAndStartBeforeAndEndAfter(
            UUID practitionerId,
            UUID appointmentId,
            LocalDateTime requestedEnd,
            LocalDateTime requestedStart
    );

    // look for active (non-cancelled) overlapping appointments for availability calculation
    // if starts before the time range end, and ends after the time range start, then is an overlapping appointment
    @Query("""
        SELECT a FROM Appointment a
        WHERE a.practitioner.id = :practitionerId
          AND a.status != :excludedStatus
          AND a.start < :rangeEnd
          AND a.end > :rangeStart
        ORDER BY a.start ASC
    """)
    List<Appointment> findByPractitionerIdAndStatusNotAndOverlappingRange(
        @Param("practitionerId") UUID practitionerId,
        @Param("excludedStatus") AppointmentStatus excludedStatus,
        @Param("rangeStart") LocalDateTime rangeStart,
        @Param("rangeEnd") LocalDateTime rangeEnd
    );

    default List<Appointment> findActiveAppointmentsByPractitionerAndRange(
        UUID practitionerId,
        LocalDateTime rangeStart,
        LocalDateTime rangeEnd
    ) {
        return findByPractitionerIdAndStatusNotAndOverlappingRange(
            practitionerId,
            AppointmentStatus.CANCELLED,
            rangeStart,
            rangeEnd
        );
    }
}
