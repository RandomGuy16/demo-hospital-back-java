package com.evergreen.generalhospital.controllers;

import com.evergreen.generalhospital.dto.patient.PatientPatchRequest;
import com.evergreen.generalhospital.dto.patient.PatientRequest;
import com.evergreen.generalhospital.dto.patient.PatientResponse;
import com.evergreen.generalhospital.models.patient.Patient;
import com.evergreen.generalhospital.models.useraccount.Role;
import com.evergreen.generalhospital.paging.SortParser;
import com.evergreen.generalhospital.services.PatientService;
import com.evergreen.generalhospital.mappers.PatientMapper;
import static com.evergreen.generalhospital.mappers.PatientMapper.patientToPatientResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/patients")
@Tag(name = "Patients", description = "Endpoints for managing patients")
@Validated
public class PatientController {
    private final PatientService patientService;

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }


    // now we are going to make some CRUD ops
    // never forget @Valid to make useful our jakarta tags


    // CREATE - POST /api/v1/patients
    // they send us a patient object
    @PostMapping
    @Operation(
        summary = "Create a new patient",
        description = "Creates a new patient from the data passed in"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Patient created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
    })
    public ResponseEntity<PatientResponse> createPatient(@RequestBody @Valid PatientRequest req,
            UriComponentsBuilder uriBuilder) {
        // the service receives the dto request and returns the patient object
        Patient created = patientService.createPatient(req);
        PatientResponse response = patientToPatientResponse(created);

        URI location = uriBuilder.path("/api/v1/patients/{id}").buildAndExpand(created.getPatientId()).toUri();

        return ResponseEntity.created(location).body(response);
    }


    // pagination is getting a lot of things, but one page at a time, basically
    // use the interfaces, from springboot, Pageable and Page
    // these replace the List<Patient> previously used

    // READ ALL - get all patients /api/v1/patients
    @GetMapping
    @Operation(
        summary = "List patients",
        description = "Returns paginated patients"
    )
    @ApiResponse(responseCode = "200", description = "Patients retrieved successfully")
    public ResponseEntity<Page<PatientResponse>> getAllPatients(
            @Parameter(description = "Zero-based page index", schema = @Schema(defaultValue = "0", minimum = "0"))
            @RequestParam(defaultValue = "0")
            @Min(0) int page,
            @Parameter(description = "Number of records per page", schema = @Schema(defaultValue = "20", minimum = "1", maximum = "100"))
            @RequestParam(defaultValue = "20")
            @Min(1) @Max(100) int size,
            @Parameter(description = "Sorting criteria in the format field,direction", example = "lastName,asc")
            @RequestParam(required = false) List<String> sort) {
        logger.info("GET /api/v1/patients Request");
        Pageable pageable = PageRequest.of(page, size, SortParser.parse(sort));
        Page<Patient> patientPage = patientService.getAllPatients(pageable);
        return ResponseEntity.ok(patientPage.map(PatientMapper::patientToPatientResponse));
    }


    // READ ONE - get patient by id /api/v1/patients/{id}
    @GetMapping("/{id}")
    @Operation(
        summary = "Get patient by ID",
        description = "Returns a patient by its unique identifier"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Patient info retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied: caller does not have ROLE_PATIENT"),
        @ApiResponse(responseCode = "404", description = "Patient record not found")
    })
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable UUID id) {
        Optional<Patient> patient = patientService.getPatientById(id);
        return patient
                .map(value -> ResponseEntity.ok(patientToPatientResponse(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // READ SELF - get patient its information for patients panel
    // /api/v1/patients/me
    @GetMapping("/me")
    @Operation(
        summary = "Get patient info",
        description = "Return a patient their information"
    )
    public ResponseEntity<PatientResponse> getPatientInfo(
        @AuthenticationPrincipal Jwt jwt,
        @Schema(hidden = true) Authentication authentication) {

        if (jwt == null || authentication == null) {
            throw new AuthenticationCredentialsNotFoundException("Authentication required");
        }

        // Sort authorities to make the response deterministic
        List<String> authorities = authentication.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .sorted()
            .toList();

        // ensure request has patient role
        if (!authorities.contains(Role.ROLE_PATIENT.name()))
            throw new AccessDeniedException("Access denied: user is not a patient");

        // get email
        String email = jwt.getClaimAsString("email");
        if (email == null || email.isBlank())
            email = jwt.getSubject();  // email == subject at the moment

        // get patient by email
        var patient = patientService.getPatientByEmail(email);
        return patient
            .map(value -> ResponseEntity.ok(patientToPatientResponse(value)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // READ BY MRN - GET /api/v1/patients/mrn/{mrn}
    @GetMapping("/mrn/{mrn}")
    @Operation(
        summary = "Get patient by MRN",
        description = "Returns a patient by medical record number"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Patient retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found"),
    })
    public ResponseEntity<PatientResponse> getPatientByMrn(@PathVariable String mrn) {
        Optional<Patient> patient = patientService.getPatientByMRN(mrn);
        return patient
                .map(value -> ResponseEntity.ok(patientToPatientResponse(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // UPDATE - PUT /api/v1/patients/{id}
    @PutMapping("/{id}")
    @Operation(
        summary = "Update a patient",
        description = "Updates an existing patient by ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Patient updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Patient not found"),
    })
    public ResponseEntity<PatientResponse> updatePatient(@PathVariable UUID id,
            @RequestBody @Valid PatientRequest req) {
        return patientService.updatePatient(id, req)
                .map(value -> ResponseEntity.ok(patientToPatientResponse(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // PARTIAL UPDATE - PATCH /api/v1/patients/{id}
    @PatchMapping("/{id}")
    @Operation(
        summary = "Partially update a patient",
        description = "Patially updates an existing patient by ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Patient patched successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Patient not found"),
    })
    public ResponseEntity<PatientResponse> patchPatient(@PathVariable UUID id,
                                                        @RequestBody @Valid PatientPatchRequest req) {
        return patientService.patchPatient(id, req)
            .map(value -> ResponseEntity.ok(patientToPatientResponse(value)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // DELETE - DELETE /api/v1/patients/{id}
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a patient",
        description = "Deletes a patient by ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Patient deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found"),
    })
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        return patientService.deletePatientById(id)
                .map(value -> ResponseEntity.noContent().<Void>build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
