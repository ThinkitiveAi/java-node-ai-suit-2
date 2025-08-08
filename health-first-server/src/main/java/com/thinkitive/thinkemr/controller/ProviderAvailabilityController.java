package com.thinkitive.thinkemr.controller;

import com.thinkitive.thinkemr.dto.ApiResponse;
import com.thinkitive.thinkemr.dto.AvailabilitySearchRequest;
import com.thinkitive.thinkemr.dto.AvailabilitySearchResponse;
import com.thinkitive.thinkemr.dto.ProviderAvailabilityRequest;
import com.thinkitive.thinkemr.dto.ProviderAvailabilityResponse;
import com.thinkitive.thinkemr.entity.ProviderAvailability;
import com.thinkitive.thinkemr.service.ProviderAvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/provider")
@RequiredArgsConstructor
@Slf4j
public class ProviderAvailabilityController {

    private final ProviderAvailabilityService availabilityService;

    @PostMapping("/availability")
    public ResponseEntity<ApiResponse<ProviderAvailabilityResponse>> createAvailability(
            @Valid @RequestBody ProviderAvailabilityRequest request) {
        
        log.info("Received availability creation request");
        
        try {
            ProviderAvailabilityResponse response = availabilityService.createAvailability(request);
            
            ApiResponse<ProviderAvailabilityResponse> apiResponse = ApiResponse.success(
                "Availability slots created successfully",
                response
            );
            
            log.info("Availability creation successful");
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
            
        } catch (Exception e) {
            log.error("Error during availability creation", e);
            throw e;
        }
    }

    @GetMapping("/{providerId}/availability")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProviderAvailability(
            @PathVariable UUID providerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) ProviderAvailability.AvailabilityStatus status,
            @RequestParam(required = false) ProviderAvailability.AppointmentType appointmentType) {
        
        log.info("Getting availability for provider: {} from {} to {}", providerId, startDate, endDate);
        
        try {
            Map<String, Object> response = availabilityService.getProviderAvailability(
                providerId, startDate, endDate, status, appointmentType);
            
            ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success(
                "Provider availability retrieved successfully",
                response
            );
            
            log.info("Provider availability retrieval successful");
            return ResponseEntity.ok(apiResponse);
            
        } catch (Exception e) {
            log.error("Error during provider availability retrieval", e);
            throw e;
        }
    }

    @PutMapping("/availability/{slotId}")
    public ResponseEntity<ApiResponse<Void>> updateAvailabilitySlot(
            @PathVariable UUID slotId,
            @Valid @RequestBody ProviderAvailabilityRequest request) {
        
        log.info("Updating availability slot: {}", slotId);
        
        try {
            availabilityService.updateAvailabilitySlot(slotId, request);
            
            ApiResponse<Void> apiResponse = ApiResponse.success(
                "Availability slot updated successfully",
                null
            );
            
            log.info("Availability slot update successful: {}", slotId);
            return ResponseEntity.ok(apiResponse);
            
        } catch (Exception e) {
            log.error("Error during availability slot update: {}", slotId, e);
            throw e;
        }
    }

    @DeleteMapping("/availability/{slotId}")
    public ResponseEntity<ApiResponse<Void>> deleteAvailabilitySlot(
            @PathVariable UUID slotId,
            @RequestParam(required = false) Boolean deleteRecurring,
            @RequestParam(required = false) String reason) {
        
        log.info("Deleting availability slot: {} with recurring: {}", slotId, deleteRecurring);
        
        try {
            availabilityService.deleteAvailabilitySlot(slotId, deleteRecurring, reason);
            
            ApiResponse<Void> apiResponse = ApiResponse.success(
                "Availability slot deleted successfully",
                null
            );
            
            log.info("Availability slot deletion successful: {}", slotId);
            return ResponseEntity.ok(apiResponse);
            
        } catch (Exception e) {
            log.error("Error during availability slot deletion: {}", slotId, e);
            throw e;
        }
    }
} 