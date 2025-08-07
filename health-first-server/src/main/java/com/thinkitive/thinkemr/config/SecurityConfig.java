package com.thinkitive.thinkemr.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Using 12 salt rounds for security
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for API endpoints
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT is stateless
            )
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/v1/provider/register", "/api/v1/provider/login").permitAll() // Public endpoints
                .requestMatchers("/api/v1/provider/specializations").permitAll() // Public endpoint
                .requestMatchers("/api/v1/provider/verify/**").permitAll() // Public verification endpoint for testing
                .requestMatchers("/h2-console/**").permitAll() // Allow H2 console
                .anyRequest().authenticated() // All other endpoints require authentication
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .headers(headers -> headers.frameOptions().disable()); // Allow H2 console frames
        
        return http.build();
    }
} 