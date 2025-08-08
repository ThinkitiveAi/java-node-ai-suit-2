package com.thinkitive.thinkemr.service;

import com.thinkitive.thinkemr.dto.PatientLoginRequest;
import com.thinkitive.thinkemr.dto.PatientLoginResponse;
import com.thinkitive.thinkemr.dto.PatientRegistrationRequest;
import com.thinkitive.thinkemr.dto.PatientRegistrationResponse;
import com.thinkitive.thinkemr.entity.Patient;
import com.thinkitive.thinkemr.exception.AuthenticationException;
import com.thinkitive.thinkemr.exception.DuplicateResourceException;
import com.thinkitive.thinkemr.exception.ValidationException;
import com.thinkitive.thinkemr.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public PatientRegistrationResponse registerPatient(PatientRegistrationRequest request) {
        log.info("Registering new patient with email: {}", request.getEmail());

        // Validate password confirmation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException("Password and confirm password do not match");
        }

        // Check for duplicate email
        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        // Check for duplicate phone number
        if (patientRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException("Phone number already registered: " + request.getPhoneNumber());
        }

        // Create patient address
        Patient.PatientAddress patientAddress = new Patient.PatientAddress(
            request.getAddress().getStreet(),
            request.getAddress().getCity(),
            request.getAddress().getState(),
            request.getAddress().getZip()
        );

        // Create emergency contact
        Patient.EmergencyContact emergencyContact = new Patient.EmergencyContact(
            request.getEmergencyContact().getName(),
            request.getEmergencyContact().getPhone(),
            request.getEmergencyContact().getRelationship()
        );

        // Create insurance info (optional)
        Patient.InsuranceInfo insuranceInfo = null;
        if (request.getInsuranceInfo() != null) {
            insuranceInfo = new Patient.InsuranceInfo(
                request.getInsuranceInfo().getProvider(),
                request.getInsuranceInfo().getPolicyNumber()
            );
        }

        // Hash password
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create patient entity
        Patient patient = new Patient();
        patient.setFirstName(sanitizeInput(request.getFirstName()));
        patient.setLastName(sanitizeInput(request.getLastName()));
        patient.setEmail(request.getEmail().toLowerCase().trim());
        patient.setPhoneNumber(request.getPhoneNumber().trim());
        patient.setPasswordHash(hashedPassword);
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setAddress(patientAddress);
        patient.setEmergencyContact(emergencyContact);
        patient.setInsuranceInfo(insuranceInfo);
        patient.setMedicalHistory(request.getMedicalHistory());
        patient.setVerificationStatus(Patient.VerificationStatus.PENDING);
        patient.setIsActive(true);

        // Save patient
        Patient savedPatient = patientRepository.save(patient);
        log.info("Patient registered successfully with ID: {}", savedPatient.getId());

        return PatientRegistrationResponse.fromPatient(savedPatient);
    }

    @Transactional(readOnly = true)
    public PatientLoginResponse loginPatient(PatientLoginRequest request) {
        log.info("Patient login attempt for email: {}", request.getEmail());

        // Find patient by email
        Patient patient = patientRepository.findByEmail(request.getEmail().toLowerCase().trim())
            .orElseThrow(() -> new AuthenticationException("Invalid credentials", "INVALID_CREDENTIALS"));

        // Check if account is active
        if (!patient.getIsActive()) {
            throw new AuthenticationException("Account is deactivated", "ACCOUNT_DEACTIVATED");
        }

        // Check if account is verified
        if (patient.getVerificationStatus() != Patient.VerificationStatus.VERIFIED) {
            throw new AuthenticationException("Account is not verified", "ACCOUNT_NOT_VERIFIED");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), patient.getPasswordHash())) {
            throw new AuthenticationException("Invalid credentials", "INVALID_CREDENTIALS");
        }

        // Generate JWT token
        String token = jwtService.generateToken(patient);
        long expiresIn = 3600; // 1 hour

        log.info("Patient login successful for email: {}", request.getEmail());
        return PatientLoginResponse.fromPatient(patient, token, expiresIn);
    }

    @Transactional
    public void verifyPatient(UUID patientId) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new ValidationException("Patient not found"));
        
        patient.setVerificationStatus(Patient.VerificationStatus.VERIFIED);
        patientRepository.save(patient);
        log.info("Patient verified with ID: {}", patientId);
    }

    private String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.trim().replaceAll("[<>\"']", "");
    }
} 