package com.thinkitive.thinkemr.controller;

import com.thinkitive.thinkemr.dto.ApiResponse;
import com.thinkitive.thinkemr.dto.AvailabilitySearchRequest;
import com.thinkitive.thinkemr.dto.AvailabilitySearchResponse;
import com.thinkitive.thinkemr.service.ProviderAvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/availability")
@RequiredArgsConstructor
@Slf4j
public class AvailabilitySearchController {

    private final ProviderAvailabilityService availabilityService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<AvailabilitySearchResponse>> searchAvailability(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String appointmentType,
            @RequestParam(required = false) Boolean insuranceAccepted,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String timezone,
            @RequestParam(defaultValue = "true") Boolean availableOnly,
            @RequestParam(required = false) String locationType) {
        
        log.info("Searching availability with criteria - date: {}, specialization: {}, location: {}", 
                date, specialization, location);
        
        try {
            AvailabilitySearchRequest request = new AvailabilitySearchRequest();
            request.setDate(date);
            request.setStartDate(startDate);
            request.setEndDate(endDate);
            request.setSpecialization(specialization);
            request.setLocation(location);
            request.setAppointmentType(appointmentType != null ? 
                com.thinkitive.thinkemr.entity.ProviderAvailability.AppointmentType.valueOf(appointmentType.toUpperCase()) : null);
            request.setInsuranceAccepted(insuranceAccepted);
            request.setMaxPrice(maxPrice);
            request.setTimezone(timezone);
            request.setAvailableOnly(availableOnly);
            request.setLocationType(locationType != null ? 
                com.thinkitive.thinkemr.entity.ProviderAvailability.Location.LocationType.valueOf(locationType.toUpperCase()) : null);

            AvailabilitySearchResponse response = availabilityService.searchAvailability(request);
            
            ApiResponse<AvailabilitySearchResponse> apiResponse = ApiResponse.success(
                "Availability search completed successfully",
                response
            );
            
            log.info("Availability search successful with {} results", response.getTotalResults());
            return ResponseEntity.ok(apiResponse);
            
        } catch (Exception e) {
            log.error("Error during availability search", e);
            throw e;
        }
    }
} 