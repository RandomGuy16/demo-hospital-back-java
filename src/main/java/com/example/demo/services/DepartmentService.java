package com.example.demo.services;

import com.example.demo.dto.DepartmentRequest;
import com.example.demo.models.Department;
import com.example.demo.repositories.DepartmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Department createDepartment(DepartmentRequest request) {
        return departmentRepository.save(new Department(request.name(), request.description()));
    }

    public Page<Department> getAllDepartments(Pageable pageable) {
        return departmentRepository.findAll(pageable);
    }

    public Optional<Department> getDepartmentById(UUID id) {
        return departmentRepository.findById(id);
    }

    public Optional<Department> updateDepartment(UUID id, DepartmentRequest request) {
        return departmentRepository.findById(id)
                .map(department -> {
                    department.setName(request.name());
                    department.setDescription(request.description());
                    return departmentRepository.save(department);
                });
    }

    public Optional<Department> deleteDepartment(UUID id) {
        return departmentRepository.findById(id)
                .map(department -> {
                    departmentRepository.delete(department);
                    return department;
                });
    }
}
