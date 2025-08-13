package com.thinkitive.thinkemr.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be in valid format")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone number must be in international format")
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @NotBlank(message = "Password hash is required")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NotNull(message = "Date of birth is required")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "street", column = @Column(name = "address_street", nullable = false, length = 200)),
        @AttributeOverride(name = "city", column = @Column(name = "address_city", nullable = false, length = 100)),
        @AttributeOverride(name = "state", column = @Column(name = "address_state", nullable = false, length = 50)),
        @AttributeOverride(name = "zip", column = @Column(name = "address_zip", nullable = false))
    })
    private PatientAddress address;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "emergency_contact_name", nullable = false, length = 100)),
        @AttributeOverride(name = "phone", column = @Column(name = "emergency_contact_phone", nullable = false)),
        @AttributeOverride(name = "relationship", column = @Column(name = "emergency_contact_relationship", nullable = false, length = 50))
    })
    private EmergencyContact emergencyContact;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "provider", column = @Column(name = "insurance_provider", length = 100)),
        @AttributeOverride(name = "policyNumber", column = @Column(name = "insurance_policy_number", length = 50))
    })
    private InsuranceInfo insuranceInfo;

    @ElementCollection
    @CollectionTable(name = "patient_medical_history", 
                    joinColumns = @JoinColumn(name = "patient_id"))
    @Column(name = "medical_condition", length = 200)
    private List<String> medicalHistory;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum Gender {
        MALE, FEMALE, OTHER
    }

    public enum VerificationStatus {
        PENDING, VERIFIED, REJECTED
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientAddress {
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

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmergencyContact {
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

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InsuranceInfo {
        @Size(max = 100, message = "Insurance provider cannot exceed 100 characters")
        private String provider;

        @Size(max = 50, message = "Policy number cannot exceed 50 characters")
        private String policyNumber;
    }
} 