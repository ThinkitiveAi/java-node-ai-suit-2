package com.thinkitive.thinkemr.dto;

import com.thinkitive.thinkemr.entity.ProviderAvailability;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilitySearchRequest {

    private LocalDate date;
    private LocalDate startDate;
    private LocalDate endDate;
    private String specialization;
    private String location; // city, state, or zip
    private ProviderAvailability.AppointmentType appointmentType;
    private Boolean insuranceAccepted;
    private BigDecimal maxPrice;
    private String timezone;
    private Boolean availableOnly = true;
    private ProviderAvailability.Location.LocationType locationType;
} 