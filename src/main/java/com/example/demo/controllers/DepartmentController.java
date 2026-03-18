package com.example.demo.controllers;

import com.example.demo.dto.DepartmentRequest;
import com.example.demo.models.Department;
import com.example.demo.services.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/departments")
@Tag(name = "Departments", description = "Endpoints for managing departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping
    @Operation(summary = "Create a department", description = "Creates a department record")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Department created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<Department> createDepartment(@RequestBody @Valid DepartmentRequest request,
            UriComponentsBuilder uriBuilder) {
        Department created = departmentService.createDepartment(request);
        URI location = uriBuilder.path("/api/v1/departments/{id}")
                .buildAndExpand(created.getDepartmentId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    @Operation(summary = "List departments", description = "Returns paginated departments")
    @ApiResponse(responseCode = "200", description = "Departments retrieved successfully")
    public ResponseEntity<Page<Department>> getAllDepartments(Pageable pageable) {
        return ResponseEntity.ok(departmentService.getAllDepartments(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID", description = "Returns a single department by its identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    public ResponseEntity<Department> getDepartmentById(@PathVariable UUID id) {
        return departmentService.getDepartmentById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update department", description = "Updates an existing department")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    public ResponseEntity<Department> updateDepartment(@PathVariable UUID id,
            @RequestBody @Valid DepartmentRequest request) {
        return departmentService.updateDepartment(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete department", description = "Deletes a department by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    public ResponseEntity<Department> deleteDepartment(@PathVariable UUID id) {
        return departmentService.deleteDepartment(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
