package com.evergreen.generalhospital.mappers;

import com.evergreen.generalhospital.dto.department.DepartmentResponse;
import com.evergreen.generalhospital.dto.department.DepartmentSummaryResponse;
import com.evergreen.generalhospital.models.department.Department;
import com.evergreen.generalhospital.models.Person;

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

    public static DepartmentSummaryResponse departmentToDepartmentSummaryResponse(Department department) {
        return new DepartmentSummaryResponse(
            department.getDepartmentId(),
            department.getName(),
            department.getDescription());
    }
}
