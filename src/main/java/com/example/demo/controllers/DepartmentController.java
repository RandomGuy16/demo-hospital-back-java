package com.example.demo.controllers;

import com.example.demo.dto.DepartmentRequest;
import com.example.demo.dto.DepartmentResponse;
import com.example.demo.models.Department;
import com.example.demo.services.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/departments")
@Tag(name = "Departments", description = "Endpoints for managing departments")
@Validated
public class DepartmentController {
    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    private DepartmentResponse departmentToDepartmentResponse(Department department) {
        return new DepartmentResponse(
                department.getDepartmentId(),
                department.getName(),
                department.getDescription(),
                department.getPractitioners().stream().map(doctor -> doctor.getFullName()).toList(),
                department.getCreatedAt(),
                department.getUpdatedAt());
    }

    @PostMapping
    @Operation(summary = "Create a department", description = "Creates a department record")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Department created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<DepartmentResponse> createDepartment(@RequestBody @Valid DepartmentRequest request,
            UriComponentsBuilder uriBuilder) {
        Department created = departmentService.createDepartment(request);
        URI location = uriBuilder.path("/api/v1/departments/{id}")
                .buildAndExpand(created.getDepartmentId())
                .toUri();
        return ResponseEntity.created(location).body(departmentToDepartmentResponse(created));
    }

    @GetMapping
    @Operation(summary = "List departments", description = "Returns paginated departments")
    @ApiResponse(responseCode = "200", description = "Departments retrieved successfully")
    public ResponseEntity<Page<DepartmentResponse>> getAllDepartments(
            @Parameter(description = "Zero-based page index", schema = @Schema(defaultValue = "0", minimum = "0"))
            @RequestParam(defaultValue = "0")
            @Min(0) int page,
            @Parameter(description = "Number of records per page", schema = @Schema(defaultValue = "20", minimum = "1", maximum = "100"))
            @RequestParam(defaultValue = "20")
            @Min(1) @Max(100) int size,
            @Parameter(description = "Sorting criteria in the format field,direction", example = "name,asc")
            @RequestParam(required = false) List<String> sort) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return ResponseEntity.ok(departmentService.getAllDepartments(pageable).map(this::departmentToDepartmentResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID", description = "Returns a single department by its identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable UUID id) {
        return departmentService.getDepartmentById(id)
                .map(department -> ResponseEntity.ok(departmentToDepartmentResponse(department)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update department", description = "Updates an existing department")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    public ResponseEntity<DepartmentResponse> updateDepartment(@PathVariable UUID id,
            @RequestBody @Valid DepartmentRequest request) {
        return departmentService.updateDepartment(id, request)
                .map(department -> ResponseEntity.ok(departmentToDepartmentResponse(department)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete department", description = "Deletes a department by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    public ResponseEntity<DepartmentResponse> deleteDepartment(@PathVariable UUID id) {
        return departmentService.deleteDepartment(id)
                .map(department -> ResponseEntity.ok(departmentToDepartmentResponse(department)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private Sort parseSort(List<String> sortParams) {
        if (sortParams == null || sortParams.isEmpty()) {
            return Sort.unsorted();
        }

        Sort sort = Sort.unsorted();
        for (String sortParam : sortParams) {
            String[] tokens = sortParam.split(",");
            String property = tokens[0].trim();
            Sort.Direction direction = tokens.length > 1
                    ? Sort.Direction.fromOptionalString(tokens[1].trim()).orElse(Sort.Direction.ASC)
                    : Sort.Direction.ASC;
            sort = sort.and(Sort.by(direction, property));
        }
        return sort;
    }
}
