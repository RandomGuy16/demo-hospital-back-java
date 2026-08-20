package com.evergreen.generalhospital.controllers;

import com.evergreen.generalhospital.dto.useraccount.CurrentUserResponse;
import com.evergreen.generalhospital.dto.useraccount.UserAccountLoginRequest;
import com.evergreen.generalhospital.dto.useraccount.UserAccountAuthenticationResponse;
import com.evergreen.generalhospital.dto.useraccount.UserAccountRegisterRequest;
import com.evergreen.generalhospital.models.useraccount.UserAccount;
import com.evergreen.generalhospital.services.JwtService;
import com.evergreen.generalhospital.services.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Authentication", description = "Authentication and current-user endpoints")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserAccountService userAccountService;

    /**
     * Creates the authentication controller dependencies.
     *
     * @param authenticationManager Spring Security entry point for username/password authentication.
     * @param jwtService service that signs and validates the API JWTs.
     * @param userAccountService service used to load and register local user accounts.
     */
    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserAccountService userAccountService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userAccountService = userAccountService;
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get current user",
            description = "Returns claims and authorities for the authenticated JWT",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Current user resolved successfully")
    /**
     * Builds the current-user payload directly from the authenticated JWT.
     *
     * @param jwt validated bearer token attached to the request.
     * @param authentication authenticated principal plus resolved authorities.
     * @return the normalized current-user response for frontend bootstrap.
     */
    public ResponseEntity<CurrentUserResponse> getCurrentUser(
            @AuthenticationPrincipal Jwt jwt,
            @Schema(hidden = true) Authentication authentication) {
        if (jwt == null || authentication == null) {
            throw new AuthenticationCredentialsNotFoundException("Authentication required");
        }

        // Sort authorities to make the response deterministic for the frontend and tests.
        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .sorted()
                .toList();

        CurrentUserResponse response = new CurrentUserResponse(
                jwt.getSubject(),
                jwt.getClaimAsString("iss"),
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("name"),
                firstNonBlank(
                        jwt.getClaimAsString("preferred_username"),
                        jwt.getClaimAsString("email"),
                        jwt.getSubject()
                ),
                authorities
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Returns the first non-empty string from the provided candidates.
     *
     * @param values ordered candidates to inspect.
     * @return the first non-blank value, or {@code null} if none is usable.
     */
    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }


    @PostMapping("/register")
    @Operation(
        summary = "Creates new user",
        description = "Creates a local user account and returns a signed JWT"
    )
    @ApiResponse(responseCode = "201", description = "New user created successfully")
    /**
     * Registers a new local user and immediately returns an access token.
     *
     * @param request user registration payload.
     * @return signed JWT for the newly created user.
     */
    public ResponseEntity<UserAccountAuthenticationResponse> register(
        @RequestBody @Valid UserAccountRegisterRequest request) {

        UserAccount created = userAccountService.registerUserAccount(request);
        String token = jwtService.generateToken(created);  // generate token
        return ResponseEntity.status(201).body(new UserAccountAuthenticationResponse(token));
    }


    @PostMapping("/login")
    @Operation(
        summary = "Login user",
        description = "Authenticates a local user and returns a signed JWT"
    )
    @ApiResponse(responseCode = "200", description = "User logged in successfully")
    /**
     * Authenticates a local user and returns a signed JWT.
     *
     * @param request login credentials.
     * @return signed JWT for the authenticated account.
     */
    public ResponseEntity<UserAccountAuthenticationResponse> login(@RequestBody @Valid UserAccountLoginRequest request) {
        // hand the email/password pair to Spring Security so the password check stays centralized.
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
            )
        );

        // Reload the domain account so token claims come from our persisted user data, not the generic UserDetails.
        UserAccount user = userAccountService.getUserAccountByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("Authenticated user account could not be loaded"));
        String token = jwtService.generateToken(user);
        UserAccountAuthenticationResponse response = new UserAccountAuthenticationResponse(token);
        return ResponseEntity.ok(response);
    }
}
