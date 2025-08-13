package com.thinkitive.thinkemr.dto;

import com.thinkitive.thinkemr.entity.Patient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientLoginResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Patient.Gender gender;
    private Patient.VerificationStatus verificationStatus;
    private String token;
    private long expiresIn;

    public static PatientLoginResponse fromPatient(Patient patient, String token, long expiresIn) {
        return new PatientLoginResponse(
            patient.getId(),
            patient.getFirstName(),
            patient.getLastName(),
            patient.getEmail(),
            patient.getPhoneNumber(),
            patient.getDateOfBirth(),
            patient.getGender(),
            patient.getVerificationStatus(),
            token,
            expiresIn
        );
    }
} 