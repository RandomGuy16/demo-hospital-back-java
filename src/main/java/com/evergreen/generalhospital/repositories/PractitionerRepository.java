package com.evergreen.generalhospital.repositories;

import com.evergreen.generalhospital.models.department.Department;
import com.evergreen.generalhospital.models.practitioner.Practitioner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PractitionerRepository extends JpaRepository<Practitioner, UUID> {
    @Override
    Optional<Practitioner> findById(UUID uuid);

    List<Practitioner> findByDepartments(List<Department> departments);

    Page<Practitioner> findByDepartments_DepartmentId(UUID departmentId, Pageable pageable);

    boolean existsBySpecialties(String specialty);

    Page<Practitioner> findBySpecialtiesContaining(String specialty, Pageable pageable);

    boolean existsByIdNumber(@NonNull String idNumber);

    // this is how you write custom repo functions in spring
    @Query("""
        SELECT DISTINCT p FROM Practitioner p
        JOIN p.departments d
        JOIN p.specialties s
        WHERE d.departmentId = :departmentId
          AND LOWER(s) ILIKE CONCAT('%', :specialty, '%')
    """)
    Page<Practitioner> findByDepartmentAndSpecialty(
        @Param("departmentId") UUID departmentId,
        @Param("specialty") String specialty,
        Pageable pageable
    );
}
