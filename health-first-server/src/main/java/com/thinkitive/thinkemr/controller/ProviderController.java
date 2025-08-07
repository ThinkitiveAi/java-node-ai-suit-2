package com.thinkitive.thinkemr.controller;

import com.thinkitive.thinkemr.dto.ApiResponse;
import com.thinkitive.thinkemr.dto.ProviderLoginRequest;
import com.thinkitive.thinkemr.dto.ProviderLoginResponse;
import com.thinkitive.thinkemr.dto.ProviderRegistrationRequest;
import com.thinkitive.thinkemr.dto.ProviderRegistrationResponse;
import com.thinkitive.thinkemr.service.ProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/provider")
@RequiredArgsConstructor
@Slf4j
public class ProviderController {

    private final ProviderService providerService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<ProviderRegistrationResponse>> registerProvider(
            @Valid @RequestBody ProviderRegistrationRequest request) {
        
        log.info("Received provider registration request for email: {}", request.getEmail());
        
        try {
            ProviderRegistrationResponse response = providerService.registerProvider(request);
            
            ApiResponse<ProviderRegistrationResponse> apiResponse = ApiResponse.success(
                "Provider registered successfully. Verification email sent.",
                response
            );
            
            log.info("Provider registration successful for email: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
            
        } catch (Exception e) {
            log.error("Error during provider registration for email: {}", request.getEmail(), e);
            throw e; // Let the global exception handler handle it
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<ProviderLoginResponse>> loginProvider(
            @Valid @RequestBody ProviderLoginRequest request) {
        
        log.info("Received provider login request for email: {}", request.getEmail());
        
        try {
            ProviderLoginResponse response = providerService.loginProvider(request);
            
            ApiResponse<ProviderLoginResponse> apiResponse = ApiResponse.success(
                "Login successful",
                response
            );
            
            log.info("Provider login successful for email: {}", request.getEmail());
            return ResponseEntity.ok(apiResponse);
            
        } catch (Exception e) {
            log.error("Error during provider login for email: {}", request.getEmail(), e);
            throw e; // Let the global exception handler handle it
        }
    }

    @PostMapping("/verify/{providerId}")
    public ResponseEntity<ApiResponse<Void>> verifyProvider(@PathVariable UUID providerId) {
        log.info("Verifying provider with ID: {}", providerId);
        
        try {
            providerService.verifyProvider(providerId);
            
            ApiResponse<Void> apiResponse = ApiResponse.success(
                "Provider verified successfully",
                null
            );
            
            log.info("Provider verification successful for ID: {}", providerId);
            return ResponseEntity.ok(apiResponse);
            
        } catch (Exception e) {
            log.error("Error during provider verification for ID: {}", providerId, e);
            throw e; // Let the global exception handler handle it
        }
    }

    @GetMapping("/specializations")
    public ResponseEntity<ApiResponse<List<String>>> getValidSpecializations() {
        List<String> specializations = providerService.getValidSpecializations();
        
        ApiResponse<List<String>> apiResponse = ApiResponse.success(
            "Valid specializations retrieved successfully",
            specializations
        );
        
        return ResponseEntity.ok(apiResponse);
    }
} 