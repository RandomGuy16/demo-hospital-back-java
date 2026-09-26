package com.evergreen.generalhospital.controllers;

import com.evergreen.generalhospital.dto.admin.AdminPractitionerRequest;
import com.evergreen.generalhospital.dto.admin.AdminPractitionerResponse;
import com.evergreen.generalhospital.dto.practitioner.PractitionerSummaryResponse;
import com.evergreen.generalhospital.mappers.PractitionerMapper;
import com.evergreen.generalhospital.models.practitioner.Practitioner;
import com.evergreen.generalhospital.models.useraccount.UserAccount;
import com.evergreen.generalhospital.paging.SortParser;
import com.evergreen.generalhospital.security.IsAdmin;
import com.evergreen.generalhospital.services.PractitionerService;
import com.evergreen.generalhospital.services.UserAccountService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin", description = "Endpoints for admin tasks")
@Validated
@IsAdmin
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final UserAccountService userAccountService;
    private final PractitionerService practitionerService;

    public AdminController(UserAccountService userAccountService,
                           PractitionerService practitionerService) {
        this.userAccountService = userAccountService;
        this.practitionerService = practitionerService;
    }

    ////////////////////////////
    //////// ENDPOINTS /////////
    ////////////////////////////

    @PostMapping("/practitioners")
    @Operation(summary = "Create practitioner staff with user account",
               description = "Onboards a practitioner, assigns departments, and provisions or updates their user account")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Practitioner onboarded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request payload"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "One or more departments not found"),
        @ApiResponse(responseCode = "409", description = "Conflict - Duplicate practitioner idNumber or user account")
    })
    public ResponseEntity<AdminPractitionerResponse> createPractitioner(
            @RequestBody @Valid AdminPractitionerRequest req,
            UriComponentsBuilder uriBuilder) {
        logger.info("POST /api/v1/admin/practitioners Request");

        UserAccount account = userAccountService.createPractitionerAccount(req);
        Practitioner practitioner = (Practitioner) account.getPerson();

        AdminPractitionerResponse response = PractitionerMapper.toAdminPractitionerResponse(practitioner, account);

        URI location = uriBuilder.path("/api/v1/practitioners/{id}")
                .buildAndExpand(practitioner.getPractitionerId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    // endpoint to get the practitioners in a list, little segmentation data to be sent here
    @GetMapping("/practitioners")
    @Operation(summary = "List practitioners (Admin)",
               description = "Returns a paginated list of practitioner summaries with optional department and specialty filtering")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Practitioners retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Department not found")
    })
    public ResponseEntity<Page<PractitionerSummaryResponse>> listPractitioners(
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) String specialty,
            @Parameter(description = "Zero-based page index", schema = @Schema(defaultValue = "0", minimum = "0"))
            @RequestParam(defaultValue = "0")
            @Min(0) int page,
            @Parameter(description = "Number of records per page", schema = @Schema(defaultValue = "20", minimum = "1", maximum = "100"))
            @RequestParam(defaultValue = "20")
            @Min(1) @Max(100) int size,
            @Parameter(description = "Sorting criteria in the format field,direction", example = "lastName,asc")
            @RequestParam(required = false) List<String> sort)
    {
        logger.info("GET /api/v1/admin/practitioners Request");

        // create the page
        Pageable pageable = PageRequest.of(page, size, SortParser.parse(sort));

        // get the payload
        Page<Practitioner> practitionerPage = practitionerService.getPractitioners(pageable, departmentId, specialty);

        // return the mapped page
        return ResponseEntity.ok(practitionerPage
            .map(PractitionerMapper::toPractitionerSummaryResponse)
        );
    }
}
