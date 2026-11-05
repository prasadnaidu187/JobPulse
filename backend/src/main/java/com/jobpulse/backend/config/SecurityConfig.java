package com.jobpulse.backend.config;

import com.jobpulse.backend.auth.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .cors(cors -> {})

                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Allow CORS preflight requests
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        // Public health check
                        .requestMatchers(
                                "/actuator/health"
                        ).permitAll()

                        // Public authentication endpoints
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/api/hello",
                                "/error"
                        ).permitAll()

                        // Public job and company listings
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/jobs",
                                "/api/jobs/**",
                                "/api/companies",
                                "/api/companies/**"
                        ).permitAll()

                        // Only administrators can create jobs
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/jobs",
                                "/api/jobs/**"
                        ).hasRole("ADMIN")

                        // Only administrators can update jobs
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/jobs",
                                "/api/jobs/**"
                        ).hasRole("ADMIN")

                        // Only administrators can delete jobs
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/jobs",
                                "/api/jobs/**"
                        ).hasRole("ADMIN")

                        // Only administrators can create companies
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/companies",
                                "/api/companies/**"
                        ).hasRole("ADMIN")

                        // Only administrators can update companies
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/companies",
                                "/api/companies/**"
                        ).hasRole("ADMIN")

                        // Only administrators can delete companies
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/companies",
                                "/api/companies/**"
                        ).hasRole("ADMIN")

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )

                // Disable default authentication mechanisms
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())

                // Configure authentication and authorization errors
                .exceptionHandling(exception -> exception

                        .authenticationEntryPoint(
                                (request, response, authException) ->
                                        response.sendError(
                                                HttpServletResponse.SC_UNAUTHORIZED,
                                                "Authentication required"
                                        )
                        )

                        .accessDeniedHandler(
                                (request, response, accessDeniedException) ->
                                        response.sendError(
                                                HttpServletResponse.SC_FORBIDDEN,
                                                "You do not have permission to access this resource"
                                        )
                        )
                )

                // Add JWT authentication filter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
