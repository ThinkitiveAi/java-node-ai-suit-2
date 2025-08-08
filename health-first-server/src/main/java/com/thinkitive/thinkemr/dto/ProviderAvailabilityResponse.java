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
public class ProviderAvailabilityResponse {

    private UUID availabilityId;
    private UUID providerId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String timezone;
    private Boolean isRecurring;
    private ProviderAvailability.RecurrencePattern recurrencePattern;
    private LocalDate recurrenceEndDate;
    private Integer slotDuration;
    private Integer breakDuration;
    private ProviderAvailability.AvailabilityStatus status;
    private Integer maxAppointmentsPerSlot;
    private Integer currentAppointments;
    private ProviderAvailability.AppointmentType appointmentType;
    private LocationDto location;
    private PricingDto pricing;
    private String notes;
    private List<String> specialRequirements;
    private Integer slotsCreated;
    private DateRangeDto dateRange;
    private Integer totalAppointmentsAvailable;

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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateRangeDto {
        private LocalDate start;
        private LocalDate end;
    }

    public static ProviderAvailabilityResponse fromAvailability(ProviderAvailability availability) {
        ProviderAvailabilityResponse response = new ProviderAvailabilityResponse();
        response.setAvailabilityId(availability.getId());
        response.setProviderId(availability.getProvider().getId());
        response.setDate(availability.getDate());
        response.setStartTime(availability.getStartTime());
        response.setEndTime(availability.getEndTime());
        response.setTimezone(availability.getTimezone());
        response.setIsRecurring(availability.getIsRecurring());
        response.setRecurrencePattern(availability.getRecurrencePattern());
        response.setRecurrenceEndDate(availability.getRecurrenceEndDate());
        response.setSlotDuration(availability.getSlotDuration());
        response.setBreakDuration(availability.getBreakDuration());
        response.setStatus(availability.getStatus());
        response.setMaxAppointmentsPerSlot(availability.getMaxAppointmentsPerSlot());
        response.setCurrentAppointments(availability.getCurrentAppointments());
        response.setAppointmentType(availability.getAppointmentType());
        response.setNotes(availability.getNotes());
        response.setSpecialRequirements(availability.getSpecialRequirements());

        if (availability.getLocation() != null) {
            response.setLocation(new LocationDto(
                availability.getLocation().getType(),
                availability.getLocation().getAddress(),
                availability.getLocation().getRoomNumber()
            ));
        }

        if (availability.getPricing() != null) {
            response.setPricing(new PricingDto(
                availability.getPricing().getBaseFee(),
                availability.getPricing().getInsuranceAccepted(),
                availability.getPricing().getCurrency()
            ));
        }

        return response;
    }
} 