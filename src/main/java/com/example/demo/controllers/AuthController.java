package com.example.demo.controllers;

import com.example.demo.dto.CurrentUserResponse;
import com.example.demo.dto.UserAccountLoginRequest;
import com.example.demo.dto.UserAccountLoginResponse;
import com.example.demo.dto.UserAccountRegisterRequest;
import com.example.demo.models.useraccount.UserAccount;
import com.example.demo.services.JwtService;
import com.example.demo.services.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
    public ResponseEntity<CurrentUserResponse> getCurrentUser(
            @AuthenticationPrincipal Jwt jwt,
            @Schema(hidden = true) Authentication authentication) {
        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .sorted()
                .toList();

        CurrentUserResponse response = new CurrentUserResponse(
                jwt.getSubject(),
                jwt.getIssuer() != null ? jwt.getIssuer().toString() : null,
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
    public ResponseEntity<UserAccountLoginResponse> register(@RequestBody @Valid UserAccountRegisterRequest request) {
        UserAccount created = userAccountService.registerUserAccount(request);
        String token = jwtService.generateToken(created);
        return ResponseEntity.status(201).body(new UserAccountLoginResponse(token));
    }

    @PostMapping("/login")
    @Operation(
        summary = "Login user",
        description = "Authenticates a local user and returns a signed JWT"
    )
    @ApiResponse(responseCode = "200", description = "User logged in successfully")
    public ResponseEntity<UserAccountLoginResponse> login(@RequestBody @Valid UserAccountLoginRequest request) {
        // hand the email/password pair to Spring Security so the password check stays centralized.
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
            )
        );

        UserAccount user = userAccountService.getUserAccountByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("Authenticated user account could not be loaded"));
        String token = jwtService.generateToken(user);
        UserAccountLoginResponse response = new UserAccountLoginResponse(token);
        return ResponseEntity.ok(response);
    }
}
