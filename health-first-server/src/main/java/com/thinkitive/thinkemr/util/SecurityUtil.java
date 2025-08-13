package com.thinkitive.thinkemr.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class SecurityUtil {

    public String getCurrentProviderEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    public String getCurrentPatientEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    public UUID getCurrentProviderId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getDetails() instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
            String providerId = (String) details.get("providerId");
            if (providerId != null) {
                try {
                    return UUID.fromString(providerId);
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid provider ID in authentication details: {}", providerId);
                }
            }
        }
        return null;
    }

    public UUID getCurrentPatientId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getDetails() instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
            String patientId = (String) details.get("patientId");
            if (patientId != null) {
                try {
                    return UUID.fromString(patientId);
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid patient ID in authentication details: {}", patientId);
                }
            }
        }
        return null;
    }

    public boolean isProviderAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && hasRole("PROVIDER");
    }

    public boolean isPatientAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && hasRole("PATIENT");
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.getAuthorities().stream()
                   .anyMatch(authority -> authority.getAuthority().equals(role));
    }
} 