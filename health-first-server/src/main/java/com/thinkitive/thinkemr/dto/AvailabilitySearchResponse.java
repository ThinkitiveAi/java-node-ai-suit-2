package com.thinkitive.thinkemr.dto;

import com.thinkitive.thinkemr.entity.ProviderAvailability;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilitySearchResponse {

    private SearchCriteriaDto searchCriteria;
    private Integer totalResults;
    private List<SearchResultDto> results;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchCriteriaDto {
        private LocalDate date;
        private String specialization;
        private String location;
        private ProviderAvailability.AppointmentType appointmentType;
        private Boolean insuranceAccepted;
        private BigDecimal maxPrice;
        private String timezone;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchResultDto {
        private ProviderDto provider;
        private List<AvailableSlotDto> availableSlots;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProviderDto {
        private UUID id;
        private String name;
        private String specialization;
        private Integer yearsOfExperience;
        private Double rating;
        private String clinicAddress;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailableSlotDto {
        private UUID slotId;
        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;
        private ProviderAvailability.AppointmentType appointmentType;
        private LocationDto location;
        private PricingDto pricing;
        private List<String> specialRequirements;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDto {
        private ProviderAvailability.Location.LocationType type;
        private String address;
        private String roomNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PricingDto {
        private BigDecimal baseFee;
        private Boolean insuranceAccepted;
        private String currency;
    }
} 