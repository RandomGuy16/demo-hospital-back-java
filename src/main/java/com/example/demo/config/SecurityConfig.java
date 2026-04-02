package com.example.demo.config;

import com.example.demo.services.JwtService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    /**
     * Configures the stateless API security chain.
     *
     * @param http Spring Security HTTP builder.
     * @param jwtAuthenticationConverter converter that maps the custom roles claim into authorities.
     * @return configured security filter chain.
     * @throws Exception if the chain cannot be built.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/api-docs/**",
                    "/api/v1/login",
                    "/api/v1/register"
                ).permitAll()
                .anyRequest().authenticated()
            ) // the api is stateless, so every protected request must carry a bearer token.
            .oauth2ResourceServer((oauth2) -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
        return http.build();
    }

    /**
     * Exposes the framework authentication manager used by the login endpoint.
     *
     * @param config Spring Security authentication configuration.
     * @return application authentication manager.
     * @throws Exception if the manager cannot be created.
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Creates the password encoder used for local accounts.
     *
     * @return BCrypt password encoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Registers DAO-based authentication against the local user-account table.
     *
     * @param userDetailsService loader for local user credentials.
     * @param passwordEncoder password hash verifier.
     * @return DAO authentication provider.
     */
    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * Builds the JWT decoder that validates tokens signed by {@link JwtService}.
     *
     * @param jwtService service exposing the shared HMAC secret.
     * @return JWT decoder.
     */
    @Bean
    JwtDecoder jwtDecoder(JwtService jwtService) {
        return NimbusJwtDecoder.withSecretKey(jwtService.getSecretKey())
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    /**
     * Maps the custom {@code roles} JWT claim into Spring Security authorities.
     *
     * @return JWT authentication converter.
     */
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Object claim = jwt.getClaims().get("roles");
            if (!(claim instanceof List<?> roles)) {
                return List.of();
            }

            // Tokens store roles as a string array, so convert each entry into a GrantedAuthority.
            return roles.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role))
                    .toList();
        });
        return converter;
    }
}
