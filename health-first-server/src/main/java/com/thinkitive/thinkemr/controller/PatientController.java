package com.thinkitive.thinkemr.controller;

import com.thinkitive.thinkemr.dto.ApiResponse;
import com.thinkitive.thinkemr.dto.PatientLoginRequest;
import com.thinkitive.thinkemr.dto.PatientLoginResponse;
import com.thinkitive.thinkemr.dto.PatientRegistrationRequest;
import com.thinkitive.thinkemr.dto.PatientRegistrationResponse;
import com.thinkitive.thinkemr.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patient")
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final PatientService patientService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<PatientRegistrationResponse>> registerPatient(
            @Valid @RequestBody PatientRegistrationRequest request) {
        
        log.info("Received patient registration request for email: {}", request.getEmail());
        
        try {
            PatientRegistrationResponse response = patientService.registerPatient(request);
            
            ApiResponse<PatientRegistrationResponse> apiResponse = ApiResponse.success(
                "Patient registered successfully. Verification email sent.",
                response
            );
            
            log.info("Patient registration successful for email: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
            
        } catch (Exception e) {
            log.error("Error during patient registration for email: {}", request.getEmail(), e);
            throw e; // Let the global exception handler handle it
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<PatientLoginResponse>> loginPatient(
            @Valid @RequestBody PatientLoginRequest request) {
        
        log.info("Received patient login request for email: {}", request.getEmail());
        
        try {
            PatientLoginResponse response = patientService.loginPatient(request);
            
            ApiResponse<PatientLoginResponse> apiResponse = ApiResponse.success(
                "Login successful",
                response
            );
            
            log.info("Patient login successful for email: {}", request.getEmail());
            return ResponseEntity.ok(apiResponse);
            
        } catch (Exception e) {
            log.error("Error during patient login for email: {}", request.getEmail(), e);
            throw e; // Let the global exception handler handle it
        }
    }

    @PostMapping("/verify/{patientId}")
    public ResponseEntity<ApiResponse<Void>> verifyPatient(@PathVariable UUID patientId) {
        log.info("Verifying patient with ID: {}", patientId);
        
        try {
            patientService.verifyPatient(patientId);
            
            ApiResponse<Void> apiResponse = ApiResponse.success(
                "Patient verified successfully",
                null
            );
            
            log.info("Patient verification successful for ID: {}", patientId);
            return ResponseEntity.ok(apiResponse);
            
        } catch (Exception e) {
            log.error("Error during patient verification for ID: {}", patientId, e);
            throw e; // Let the global exception handler handle it
        }
    }
} 