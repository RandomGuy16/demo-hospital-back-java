package com.example.demo.repositories;

import com.example.demo.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    @Override
    List<Appointment> findAll();

    List<Appointment> findByPractitioner_PractitionerId(UUID practitionerId);

    List<Appointment> findByPatient_PatientId(UUID patientId);

    List<Appointment> findByDepartment_DepartmentId(UUID departmentId);
}
