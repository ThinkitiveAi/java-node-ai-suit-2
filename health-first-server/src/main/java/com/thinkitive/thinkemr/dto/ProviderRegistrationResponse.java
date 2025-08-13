package com.thinkitive.thinkemr.dto;

import com.thinkitive.thinkemr.entity.Provider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderRegistrationResponse {
    private UUID providerId;
    private String email;
    private Provider.VerificationStatus verificationStatus;

    public static ProviderRegistrationResponse fromProvider(Provider provider) {
        return new ProviderRegistrationResponse(
            provider.getId(),
            provider.getEmail(),
            provider.getVerificationStatus()
        );
    }
} 