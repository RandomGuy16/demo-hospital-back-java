package com.example.demo.controllers;

import com.example.demo.dto.PractitionerRequest;
import com.example.demo.models.Practitioner;
import com.example.demo.services.PractitionerService;
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
@RequestMapping("/api/v1/practitioners")
@Tag(name = "Practitioners", description = "Endpoints for managing practitioners")
public class PractitionerController {
    private final PractitionerService practitionerService;

    public PractitionerController(PractitionerService practitionerService) {
        this.practitionerService = practitionerService;
    }

    @PostMapping
    @Operation(summary = "Create a practitioner", description = "Creates a practitioner record")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Practitioner created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<Practitioner> createPractitioner(@RequestBody @Valid PractitionerRequest request,
            UriComponentsBuilder uriBuilder) {
        Practitioner created = practitionerService.createPractitioner(request);
        URI location = uriBuilder.path("/api/v1/practitioners/{id}")
                .buildAndExpand(created.getPractitionerId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    @Operation(summary = "List practitioners", description = "Returns paginated practitioners")
    @ApiResponse(responseCode = "200", description = "Practitioners retrieved successfully")
    public ResponseEntity<Page<Practitioner>> getAllPractitioners(Pageable pageable) {
        return ResponseEntity.ok(practitionerService.getAllPractitioners(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get practitioner by ID", description = "Returns a single practitioner by its identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Practitioner retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Practitioner not found")
    })
    public ResponseEntity<Practitioner> getPractitionerById(@PathVariable UUID id) {
        return practitionerService.getPractitionerById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update practitioner", description = "Updates an existing practitioner")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Practitioner updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Practitioner not found")
    })
    public ResponseEntity<Practitioner> updatePractitioner(@PathVariable UUID id,
            @RequestBody @Valid PractitionerRequest request) {
        return practitionerService.updatePractitioner(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete practitioner", description = "Deletes a practitioner by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Practitioner deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Practitioner not found")
    })
    public ResponseEntity<Practitioner> deletePractitioner(@PathVariable UUID id) {
        return practitionerService.deletePractitioner(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
