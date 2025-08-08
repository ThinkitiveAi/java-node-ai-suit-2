package com.thinkitive.thinkemr.dto;

import com.thinkitive.thinkemr.entity.Patient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientRegistrationRequest {

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

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Patient.Gender gender;

    @Valid
    @NotNull(message = "Address is required")
    private AddressDto address;

    @Valid
    @NotNull(message = "Emergency contact is required")
    private EmergencyContactDto emergencyContact;

    @Valid
    private InsuranceInfoDto insuranceInfo;

    private List<String> medicalHistory;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDto {
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmergencyContactDto {
        @NotBlank(message = "Emergency contact name is required")
        @Size(max = 100, message = "Emergency contact name cannot exceed 100 characters")
        private String name;

        @NotBlank(message = "Emergency contact phone is required")
        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Emergency contact phone must be in international format")
        private String phone;

        @NotBlank(message = "Emergency contact relationship is required")
        @Size(max = 50, message = "Emergency contact relationship cannot exceed 50 characters")
        private String relationship;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InsuranceInfoDto {
        @Size(max = 100, message = "Insurance provider cannot exceed 100 characters")
        private String provider;

        @Size(max = 50, message = "Policy number cannot exceed 50 characters")
        private String policyNumber;
    }
} 