package com.example.demo.testsupport.factories;

import com.example.demo.models.department.Department;
import com.example.demo.repositories.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class DepartmentFactory {

    @Autowired
    private DepartmentRepository departmentRepository;

    public Department saveDepartment(String name, String description) {
        return departmentRepository.save(new Department(name, description));
    }
}
