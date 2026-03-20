package com.example.demo.mappers;

import com.example.demo.dto.DepartmentResponse;
import com.example.demo.models.Department;
import com.example.demo.models.Person;

public class DepartmentMapper {

    public static DepartmentResponse departmentToDepartmentResponse(Department department) {
        return new DepartmentResponse(
            department.getDepartmentId(),
            department.getName(),
            department.getDescription(),
            department.getPractitioners().stream().map(Person::getFullName).toList(),
            department.getCreatedAt(),
            department.getUpdatedAt());
    }
}
