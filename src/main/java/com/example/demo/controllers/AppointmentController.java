package com.example.demo.controllers;

import com.example.demo.dto.AppointmentRequest;
import com.example.demo.models.Appointment;
import com.example.demo.services.AppointmentService;
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
@RequestMapping("/api/v1/appointments")
@Tag(name = "Appointments", description = "Endpoints for managing appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @Operation(summary = "Create an appointment", description = "Creates an appointment record")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Appointment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<Appointment> createAppointment(@RequestBody @Valid AppointmentRequest request,
            UriComponentsBuilder uriBuilder) {
        Appointment created = appointmentService.createAppointment(request);
        URI location = uriBuilder.path("/api/v1/appointments/{id}")
                .buildAndExpand(created.getAppointmentId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    @Operation(summary = "List appointments", description = "Returns paginated appointments")
    @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully")
    public ResponseEntity<Page<Appointment>> getAllAppointments(Pageable pageable) {
        return ResponseEntity.ok(appointmentService.getAllAppointments(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID", description = "Returns a single appointment by its identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable UUID id) {
        return appointmentService.getAppointmentById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update appointment", description = "Updates an existing appointment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<Appointment> updateAppointment(@PathVariable UUID id,
            @RequestBody @Valid AppointmentRequest request) {
        return appointmentService.updateAppointment(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete appointment", description = "Deletes an appointment by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<Appointment> deleteAppointment(@PathVariable UUID id) {
        return appointmentService.deleteAppointment(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
