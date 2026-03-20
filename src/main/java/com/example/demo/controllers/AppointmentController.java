package com.example.demo.controllers;

import com.example.demo.dto.AppointmentRequest;
import com.example.demo.dto.AppointmentResponse;
import com.example.demo.dto.ErrorCode;
import com.example.demo.dto.ErrorResponse;
import com.example.demo.models.Appointment;
import com.example.demo.services.AppointmentService;
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
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/appointments")
@Tag(name = "Appointments", description = "Endpoints for managing appointments")
@Validated
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    private AppointmentResponse appointmentToAppointmentResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getAppointmentId(),
                appointment.getPatientId(),
                appointment.getPractitionerId(),
                appointment.getDepartmentId(),
                appointment.getStart(),
                appointment.getEnd(),
                appointment.getStatus());
    }

    @PostMapping
    @Operation(summary = "Create an appointment", description = "Creates an appointment record")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Appointment created successfully"),
            @ApiResponse(responseCode = "404", description = "Patient, practitioner or department not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<AppointmentResponse> createAppointment(@RequestBody @Valid AppointmentRequest request,
            UriComponentsBuilder uriBuilder) {
        Appointment created = appointmentService.createAppointment(request);
        URI location = uriBuilder.path("/api/v1/appointments/{id}")
            .buildAndExpand(created.getAppointmentId())
            .toUri();
        return ResponseEntity.created(location).body(appointmentToAppointmentResponse(created));

    }

    @GetMapping
    @Operation(summary = "List appointments", description = "Returns paginated appointments")
    @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully")
    public ResponseEntity<Page<AppointmentResponse>> getAllAppointments(
            @Parameter(description = "Zero-based page index", schema = @Schema(defaultValue = "0", minimum = "0"))
            @RequestParam(defaultValue = "0")
            @Min(0) int page,
            @Parameter(description = "Number of records per page", schema = @Schema(defaultValue = "20", minimum = "1", maximum = "100"))
            @RequestParam(defaultValue = "20")
            @Min(1) @Max(100) int size,
            @Parameter(description = "Sorting criteria in the format field,direction", example = "start,asc")
            @RequestParam(required = false) List<String> sort) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return ResponseEntity.ok(appointmentService.getAllAppointments(pageable).map(this::appointmentToAppointmentResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID", description = "Returns a single appointment by its identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable UUID id) {
        return appointmentService.getAppointmentById(id)
                .map(appointment -> ResponseEntity.ok(appointmentToAppointmentResponse(appointment)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update appointment", description = "Updates an existing appointment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<AppointmentResponse> updateAppointment(@PathVariable UUID id,
            @RequestBody @Valid AppointmentRequest request) {
        return appointmentService.updateAppointment(id, request)
                .map(appointment -> ResponseEntity.ok(appointmentToAppointmentResponse(appointment)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete appointment", description = "Deletes an appointment by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Appointment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<Void> deleteAppointment(@PathVariable UUID id) {
        return appointmentService.deleteAppointment(id)
                .map(appointment -> ResponseEntity.noContent().<Void>build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private Sort parseSort(List<String> sortParams) {
        if (sortParams == null || sortParams.isEmpty()) {
            return Sort.unsorted();
        }

        Sort sort = Sort.unsorted();
        for (int i = 0; i < sortParams.size(); i++) {
            String sortParam = sortParams.get(i);
            String[] tokens = sortParam.split(",");
            String property = tokens[0].trim();
            Sort.Direction direction = Sort.Direction.ASC;

            if (tokens.length > 1) {
                direction = Sort.Direction.fromOptionalString(tokens[1].trim()).orElse(Sort.Direction.ASC);
            } else if (i + 1 < sortParams.size()) {
                Sort.Direction nextDirection = Sort.Direction.fromOptionalString(sortParams.get(i + 1).trim()).orElse(null);
                if (nextDirection != null) {
                    direction = nextDirection;
                    i++;
                }
            }

            sort = sort.and(Sort.by(direction, property));
        }
        return sort;
    }
}
