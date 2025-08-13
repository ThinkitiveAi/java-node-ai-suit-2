package com.thinkitive.thinkemr.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderRegistrationRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be in valid format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone number must be in international format")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Password must contain at least 8 characters, one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    @NotBlank(message = "Specialization is required")
    @Size(min = 3, max = 100, message = "Specialization must be between 3 and 100 characters")
    private String specialization;

    @NotBlank(message = "License number is required")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "License number must be alphanumeric")
    private String licenseNumber;

    @Min(value = 0, message = "Years of experience must be at least 0")
    @Max(value = 50, message = "Years of experience cannot exceed 50")
    private Integer yearsOfExperience;

    @Valid
    @NotNull(message = "Clinic address is required")
    private ClinicAddressDto clinicAddress;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClinicAddressDto {
        @NotBlank(message = "Street address is required")
        @Size(max = 200, message = "Street address cannot exceed 200 characters")
        private String street;

        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City cannot exceed 100 characters")
        private String city;

        @NotBlank(message = "State is required")
        @Size(max = 50, message = "State cannot exceed 50 characters")
        private String state;

        @NotBlank(message = "ZIP code is required")
        @Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "ZIP code must be in valid format (e.g., 12345 or 12345-6789)")
        private String zip;
    }
} 