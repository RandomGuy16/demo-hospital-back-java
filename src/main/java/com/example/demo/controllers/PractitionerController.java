package com.example.demo.controllers;

import com.example.demo.dto.PractitionerRequest;
import com.example.demo.dto.PractitionerResponse;
import com.example.demo.models.Practitioner;
import com.example.demo.services.PractitionerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/api/v1/practitioners")
@Tag(name = "Practitioners", description = "Endpoints for managing practitioners")
@Validated
public class PractitionerController {

    private static final Logger logger = LoggerFactory.getLogger(PractitionerController.class);

    private final PractitionerService practitionerService;

    private PractitionerResponse practitionerToPractitionerResponse(Practitioner practitioner) {
        return new PractitionerResponse(
            practitioner.getPractitionerId(),
            practitioner.getFirstName(),
            practitioner.getLastName(),
            practitioner.getDateOfBirth(),
            practitioner.getGender(),
            practitioner.getPhoneNumber(),
            practitioner.getContacts(),
            practitioner.getSpecialties(),
            practitioner.getDepartments().stream().map(department -> department.getName()).toList()
        );
    }

    public PractitionerController(PractitionerService practitionerService) {
        this.practitionerService = practitionerService;
    }

    @PostMapping
    @Operation(summary = "Create a practitioner", description = "Creates a practitioner record")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Practitioner created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<PractitionerResponse> createPractitioner(@RequestBody @Valid PractitionerRequest request,
            UriComponentsBuilder uriBuilder) {
        Practitioner created = practitionerService.createPractitioner(request);
        PractitionerResponse response = practitionerToPractitionerResponse(created);
        URI location = uriBuilder.path("/api/v1/practitioners/{id}")
                .buildAndExpand(created.getPractitionerId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "List practitioners", description = "Returns paginated practitioners")
    @ApiResponse(responseCode = "200", description = "Practitioners retrieved successfully")
    public ResponseEntity<Page<PractitionerResponse>> getAllPractitioners(
            @Parameter(description = "Zero-based page index", schema = @Schema(defaultValue = "0", minimum = "0"))
            @RequestParam(defaultValue = "0")
            @Min(0) int page,
            @Parameter(description = "Number of records per page", schema = @Schema(defaultValue = "20", minimum = "1", maximum = "100"))
            @RequestParam(defaultValue = "20")
            @Min(1) @Max(100) int size,
            @Parameter(description = "Sorting criteria in the format field,direction", example = "lastName,asc")
            @RequestParam(required = false) List<String> sort) {
        logger.info("GET /pai/v1/practitioners Request");
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        Page<Practitioner> practitionerPage = practitionerService.getAllPractitioners(pageable);
        return ResponseEntity.ok(practitionerPage.map(this::practitionerToPractitionerResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get practitioner by ID", description = "Returns a single practitioner by its identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Practitioner retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Practitioner not found")
    })
    public ResponseEntity<PractitionerResponse> getPractitionerById(@PathVariable UUID id) {
        return practitionerService.getPractitionerById(id)
                .map(practitioner -> ResponseEntity.ok(practitionerToPractitionerResponse(practitioner)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update practitioner", description = "Updates an existing practitioner")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Practitioner updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Practitioner not found")
    })
    public ResponseEntity<PractitionerResponse> updatePractitioner(@PathVariable UUID id,
            @RequestBody @Valid PractitionerRequest request) {
        return practitionerService.updatePractitioner(id, request)
                .map(practitioner -> ResponseEntity.ok(practitionerToPractitionerResponse(practitioner)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete practitioner", description = "Deletes a practitioner by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Practitioner deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Practitioner not found")
    })
    public ResponseEntity<PractitionerResponse> deletePractitioner(@PathVariable UUID id) {
        return practitionerService.deletePractitioner(id)
                .map(practitioner -> ResponseEntity.ok(practitionerToPractitionerResponse(practitioner)))
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
