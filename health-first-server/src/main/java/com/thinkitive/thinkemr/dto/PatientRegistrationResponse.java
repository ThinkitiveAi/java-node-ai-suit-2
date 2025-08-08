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
public class PatientRegistrationResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Patient.Gender gender;
    private Patient.VerificationStatus verificationStatus;

    public static PatientRegistrationResponse fromPatient(Patient patient) {
        return new PatientRegistrationResponse(
            patient.getId(),
            patient.getFirstName(),
            patient.getLastName(),
            patient.getEmail(),
            patient.getPhoneNumber(),
            patient.getDateOfBirth(),
            patient.getGender(),
            patient.getVerificationStatus()
        );
    }
} 