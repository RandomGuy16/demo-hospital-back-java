package com.example.demo.repositories;

import com.example.demo.models.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    @Override
    Optional<Department> findById(UUID departmentId);

    Optional<Department> findByName(String name);
}
