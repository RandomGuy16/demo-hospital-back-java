package com.evergreen.generalhospital.controllers;

import com.evergreen.generalhospital.dto.admin.AdminPractitionerRequest;
import com.evergreen.generalhospital.dto.admin.AdminPractitionerResponse;
import com.evergreen.generalhospital.mappers.PractitionerMapper;
import com.evergreen.generalhospital.models.practitioner.Practitioner;
import com.evergreen.generalhospital.models.useraccount.UserAccount;
import com.evergreen.generalhospital.services.PractitionerService;
import com.evergreen.generalhospital.services.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin", description = "Endpoints for admin tasks")
@Validated
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
    private final UserAccountService userAccountService;

    public AdminController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
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
        UserAccount account = userAccountService.createPractitionerAccount(req);
        Practitioner practitioner = (Practitioner) account.getPerson();

        AdminPractitionerResponse response = PractitionerMapper.toAdminPractitionerResponse(practitioner, account);

        URI location = uriBuilder.path("/api/v1/practitioners/{id}")
                .buildAndExpand(practitioner.getPractitionerId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }
}
