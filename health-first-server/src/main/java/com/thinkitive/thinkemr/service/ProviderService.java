package com.thinkitive.thinkemr.service;

import com.thinkitive.thinkemr.dto.ProviderLoginRequest;
import com.thinkitive.thinkemr.dto.ProviderLoginResponse;
import com.thinkitive.thinkemr.dto.ProviderRegistrationRequest;
import com.thinkitive.thinkemr.dto.ProviderRegistrationResponse;
import com.thinkitive.thinkemr.entity.ClinicAddress;
import com.thinkitive.thinkemr.entity.Provider;
import com.thinkitive.thinkemr.exception.AuthenticationException;
import com.thinkitive.thinkemr.exception.DuplicateResourceException;
import com.thinkitive.thinkemr.exception.ValidationException;
import com.thinkitive.thinkemr.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProviderService {

    private final ProviderRepository providerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Predefined list of valid specializations
    private static final List<String> VALID_SPECIALIZATIONS = Arrays.asList(
        "Cardiology", "Dermatology", "Endocrinology", "Gastroenterology", "General Practice",
        "Hematology", "Infectious Disease", "Internal Medicine", "Neurology", "Obstetrics and Gynecology",
        "Oncology", "Ophthalmology", "Orthopedics", "Otolaryngology", "Pediatrics",
        "Psychiatry", "Pulmonology", "Radiology", "Rheumatology", "Urology",
        "Emergency Medicine", "Family Medicine", "Geriatrics", "Nephrology", "Pathology"
    );

    @Transactional
    public ProviderRegistrationResponse registerProvider(ProviderRegistrationRequest request) {
        log.info("Registering new provider with email: {}", request.getEmail());

        // Validate password confirmation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException("Password and confirm password do not match");
        }

        // Validate specialization
        if (!VALID_SPECIALIZATIONS.contains(request.getSpecialization())) {
            throw new ValidationException("Invalid specialization. Please choose from the predefined list");
        }

        // Check for duplicate email
        if (providerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        // Check for duplicate phone number
        if (providerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException("Phone number already registered: " + request.getPhoneNumber());
        }

        // Check for duplicate license number
        if (providerRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateResourceException("License number already registered: " + request.getLicenseNumber());
        }

        // Create clinic address
        ClinicAddress clinicAddress = new ClinicAddress(
            request.getClinicAddress().getStreet(),
            request.getClinicAddress().getCity(),
            request.getClinicAddress().getState(),
            request.getClinicAddress().getZip()
        );

        // Hash password
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create provider entity
        Provider provider = new Provider();
        provider.setFirstName(sanitizeInput(request.getFirstName()));
        provider.setLastName(sanitizeInput(request.getLastName()));
        provider.setEmail(request.getEmail().toLowerCase().trim());
        provider.setPhoneNumber(request.getPhoneNumber().trim());
        provider.setPasswordHash(hashedPassword);
        provider.setSpecialization(sanitizeInput(request.getSpecialization()));
        provider.setLicenseNumber(request.getLicenseNumber().toUpperCase().trim());
        provider.setYearsOfExperience(request.getYearsOfExperience());
        provider.setClinicAddress(clinicAddress);
        provider.setVerificationStatus(Provider.VerificationStatus.PENDING);
        provider.setIsActive(true);

        // Save provider
        Provider savedProvider = providerRepository.save(provider);
        log.info("Provider registered successfully with ID: {}", savedProvider.getId());

        return ProviderRegistrationResponse.fromProvider(savedProvider);
    }

    @Transactional(readOnly = true)
    public ProviderLoginResponse loginProvider(ProviderLoginRequest request) {
        log.info("Provider login attempt for email: {}", request.getEmail());

        // Find provider by email
        Provider provider = providerRepository.findByEmail(request.getEmail().toLowerCase().trim())
            .orElseThrow(() -> new AuthenticationException("Invalid credentials", "INVALID_CREDENTIALS"));

        // Check if account is active
        if (!provider.getIsActive()) {
            throw new AuthenticationException("Account is deactivated", "ACCOUNT_DEACTIVATED");
        }

        // Check if account is verified
        if (provider.getVerificationStatus() != Provider.VerificationStatus.VERIFIED) {
            throw new AuthenticationException("Account is not verified", "ACCOUNT_NOT_VERIFIED");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), provider.getPasswordHash())) {
            throw new AuthenticationException("Invalid credentials", "INVALID_CREDENTIALS");
        }

        // Generate JWT token
        String token = jwtService.generateToken(provider);
        long expiresIn = 3600; // 1 hour

        log.info("Provider login successful for email: {}", request.getEmail());
        return ProviderLoginResponse.fromProvider(provider, token, expiresIn);
    }

    @Transactional
    public void verifyProvider(UUID providerId) {
        Provider provider = providerRepository.findById(providerId)
            .orElseThrow(() -> new ValidationException("Provider not found"));
        
        provider.setVerificationStatus(Provider.VerificationStatus.VERIFIED);
        providerRepository.save(provider);
        log.info("Provider verified with ID: {}", providerId);
    }

    private String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        // Remove leading/trailing whitespace and escape HTML characters
        return input.trim()
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("&", "&amp;");
    }

    public List<String> getValidSpecializations() {
        return VALID_SPECIALIZATIONS;
    }
} 