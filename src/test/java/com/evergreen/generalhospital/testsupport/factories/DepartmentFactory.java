package com.evergreen.generalhospital.testsupport.factories;

import com.evergreen.generalhospital.models.department.Department;
import com.evergreen.generalhospital.repositories.DepartmentRepository;
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
