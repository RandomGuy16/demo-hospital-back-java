package com.evergreen.generalhospital.services;

import com.evergreen.generalhospital.models.useraccount.Role;
import com.evergreen.generalhospital.models.useraccount.UserAccount;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMillis;
    private final String issuer;

    /**
     * Creates the JWT service dependencies.
     *
     * @param secretKey base64-encoded HMAC secret used to sign and verify tokens.
     * @param expirationMillis token lifetime in milliseconds.
     * @param issuer issuer claim written into generated tokens.
     */
    public JwtService(
        @Value("${security.jwt.secret}") String secretKey,
        @Value("${security.jwt.expiration-ms}") long expirationMillis,
        @Value("${security.jwt.issuer}") String issuer) {

        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.expirationMillis = expirationMillis;
        this.issuer = issuer;
    }

    /**
     * Generates a signed JWT for a persisted local user account.
     *
     * @param userAccount authenticated local user account.
     * @return signed compact JWT string.
     */
    public String generateToken(UserAccount userAccount) {
        Instant now = Instant.now();

        List<String> roles = userAccount.getRoles().stream().map(Role::name).toList();

        return Jwts.builder()
            .subject(userAccount.getEmail())
            .issuer(issuer)
            .claim("roles", roles)
            .claim("email", userAccount.getEmail())
            .claim("name", userAccount.getDisplayName())
            .claim("preferred_username", userAccount.getUsername())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(expirationMillis)))
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact();
    }

    /**
     * Generates a signed token with data provided by google oauth
     * method meant to be called when a user requests an onboarding
     * session right after coming from google oauth
     *
     * @param email Email
     * @param name name
     * @param googleSub Provider subject
     * @return token
     *
     */
    public record OnboardingClaims(String email, String name, String googleSub) {}

    /**
     * Generates a signed temporary onboarding token containing Google profile data.
     * This token is issued to new users to allow them to complete their medical profile
     * before receiving a full API JWT.
     *
     * @param email Email resolved from Google.
     * @param name Name resolved from Google.
     * @param googleSub Google unique subject ID.
     * @return signed onboarding JWT.
     */
    public String generateOnboardingToken(String email, String name, String googleSub) {
        Instant now = Instant.now();
        // Limit onboarding token lifetime to 30 minutes (or expirationMillis if shorter)
        long onboardingTtl = Math.min(expirationMillis, 30L * 60L * 1000L);

        return Jwts.builder()
            .subject(email)
            .issuer(issuer)
            .claim("token_type", "ONBOARDING")
            .claim("email", email)
            .claim("name", name)
            .claim("provider_subject", googleSub)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(onboardingTtl)))
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact();
    }

    /**
     * Validates an onboarding token and extracts the Google identity claims.
     *
     * @param token signed onboarding JWT string.
     * @return parsed onboarding identity data.
     * @throws IllegalArgumentException if the token is not a valid onboarding token.
     */
    public OnboardingClaims extractOnboardingClaims(String token) {
        Claims claims = extractAllClaims(token);
        String tokenType = claims.get("token_type", String.class);
        if (!"ONBOARDING".equals(tokenType)) {
            throw new IllegalArgumentException("Token is not an onboarding token");
        }
        return new OnboardingClaims(
            claims.get("email", String.class),
            claims.get("name", String.class),
            claims.get("provider_subject", String.class)
        );
    }

    /**
     * Extracts the subject claim from a signed JWT.
     *
     * @param token signed JWT string.
     * @return token subject.
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Validates that the token belongs to the provided user and is not expired.
     *
     * @param token signed JWT string.
     * @param userDetails Spring Security user details to compare against the token subject.
     * @return {@code true} when the token is valid for that user.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername())
            && extractAllClaims(token).getExpiration().after(new Date());
    }

    /**
     * Parses every claim from the signed JWT.
     *
     * @param token signed JWT string.
     * @return parsed JWT claims.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    /**
     * Exposes the raw HMAC key so Spring Security can build the matching decoder.
     *
     * @return signing secret key.
     */
    public SecretKey getSecretKey() {
        return secretKey;
    }
}
