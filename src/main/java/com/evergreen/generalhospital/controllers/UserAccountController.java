package com.evergreen.generalhospital.controllers;

import com.evergreen.generalhospital.dto.useraccount.UserAccountRequest;
import com.evergreen.generalhospital.dto.useraccount.UserAccountResponse;
import com.evergreen.generalhospital.models.useraccount.UserAccount;
import com.evergreen.generalhospital.services.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static com.evergreen.generalhospital.mappers.UserAccountMapper.userAccountToUserAccountResponse;

@RestController
@RequestMapping("/api/v1/user-accounts")
@Tag(name = "User accounts", description = "Endpoints for managing users")
@Validated
public class UserAccountController {
    private final UserAccountService userAccountService;

    /**
     * Creates the user-account controller dependencies.
     *
     * @param userAccountService service handling user-account persistence and validation.
     */
    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping
    @Operation(summary = "Create a user account", description = "Creates a user account record")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User account created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    /**
     * Future feature: Administrative account creation.
     * Reserved for upcoming administrative functionality in the dedicated admin branch.
     * Creates a user account record from an explicit administrative request.
     *
     * @param request user-account payload.
     * @param uriBuilder helper used to build the resource location header.
     * @return created user-account response plus location header.
     */
    public ResponseEntity<UserAccountResponse> createUserAccount(@RequestBody @Valid UserAccountRequest request,
                                                                  UriComponentsBuilder uriBuilder) {
        UserAccount created = userAccountService.adminCreateUserAccount(request);
        UserAccountResponse response = userAccountToUserAccountResponse(created);
        URI location = uriBuilder.path("/api/v1/user-accounts/{id}")
            .buildAndExpand(created.getId())
            .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "List user accounts", description = "Returns all user accounts")
    @ApiResponse(responseCode = "200", description = "User accounts retrieved successfully")
    /**
     * Lists all user accounts.
     *
     * @return all persisted user accounts mapped to API responses.
     */
    public ResponseEntity<List<UserAccountResponse>> getAllUserAccounts() {
        List<UserAccountResponse> response = userAccountService.getAllUserAccounts()
                .stream()
                .map(UserAccountController::mapToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user account by id", description = "Returns a single user account")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User account retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User account not found")
    })
    /**
     * Fetches a single user account by id.
     *
     * @param id user-account identifier.
     * @return the mapped response when found, otherwise 404.
     */
    public ResponseEntity<UserAccountResponse> getUserAccountById(@PathVariable UUID id) {
        return userAccountService.getUserAccountById(id)
                .map(UserAccountController::mapToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Maps the domain entity to its outward-facing DTO.
     *
     * @param userAccount persisted user account.
     * @return API response DTO.
     */
    private static UserAccountResponse mapToResponse(UserAccount userAccount) {
        return userAccountToUserAccountResponse(userAccount);
    }
}
