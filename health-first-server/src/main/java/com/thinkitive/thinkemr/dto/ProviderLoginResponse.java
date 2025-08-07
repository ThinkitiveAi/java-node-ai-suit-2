package com.thinkitive.thinkemr.dto;

import com.thinkitive.thinkemr.entity.Provider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderLoginResponse {
    private String accessToken;
    private long expiresIn;
    private String tokenType;
    private ProviderData provider;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProviderData {
        private String id;
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private String specialization;
        private String licenseNumber;
        private Integer yearsOfExperience;
        private String verificationStatus;
        private Boolean isActive;
    }

    public static ProviderLoginResponse fromProvider(Provider provider, String token, long expiresIn) {
        ProviderData providerData = new ProviderData();
        providerData.setId(provider.getId().toString());
        providerData.setFirstName(provider.getFirstName());
        providerData.setLastName(provider.getLastName());
        providerData.setEmail(provider.getEmail());
        providerData.setPhoneNumber(provider.getPhoneNumber());
        providerData.setSpecialization(provider.getSpecialization());
        providerData.setLicenseNumber(provider.getLicenseNumber());
        providerData.setYearsOfExperience(provider.getYearsOfExperience());
        providerData.setVerificationStatus(provider.getVerificationStatus().toString());
        providerData.setIsActive(provider.getIsActive());

        return new ProviderLoginResponse(token, expiresIn, "Bearer", providerData);
    }
} 