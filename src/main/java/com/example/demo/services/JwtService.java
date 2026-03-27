package com.example.demo.services;

import com.example.demo.models.useraccount.UserAccount;
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

    public JwtService(
        @Value("${security.jwt.secret}") String secretKey,
        @Value("${security.jwt.expiration-ms}") long expirationMillis,
        @Value("${security.jwt.issuer}") String issuer) {

        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.expirationMillis = expirationMillis;
        this.issuer = issuer;
    }

    // include the basic profile claims the frontend needs after login.
    public String generateToken(UserAccount userAccount) {
        Instant now = Instant.now();

        List<String> roles = List.of(userAccount.getRole().name());

        return Jwts.builder()
            .subject(userAccount.getEmail())
            .issuer(issuer)
            .claim("roles", roles)
            .claim("email", userAccount.getEmail())
            .claim("name", userAccount.getDisplayName())
            .claim("preferred_username", userAccount.getUsername())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(expirationMillis)))
            .signWith(secretKey)
            .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername())
            && extractAllClaims(token).getExpiration().after(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }
}
