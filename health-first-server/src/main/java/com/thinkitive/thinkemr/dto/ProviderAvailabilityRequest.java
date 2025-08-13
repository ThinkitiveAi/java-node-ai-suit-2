package com.thinkitive.thinkemr.dto;

import com.thinkitive.thinkemr.entity.ProviderAvailability;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderAvailabilityRequest {

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotBlank(message = "Timezone is required")
    private String timezone;

    private Boolean isRecurring = false;

    private ProviderAvailability.RecurrencePattern recurrencePattern;

    private LocalDate recurrenceEndDate;

    @Min(value = 15, message = "Slot duration must be at least 15 minutes")
    @Max(value = 480, message = "Slot duration cannot exceed 8 hours")
    private Integer slotDuration = 30;

    @Min(value = 0, message = "Break duration cannot be negative")
    @Max(value = 120, message = "Break duration cannot exceed 2 hours")
    private Integer breakDuration = 0;

    @Min(value = 1, message = "Max appointments per slot must be at least 1")
    @Max(value = 10, message = "Max appointments per slot cannot exceed 10")
    private Integer maxAppointmentsPerSlot = 1;

    @NotNull(message = "Appointment type is required")
    private ProviderAvailability.AppointmentType appointmentType;

    @Valid
    @NotNull(message = "Location is required")
    private LocationDto location;

    @Valid
    private PricingDto pricing;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    private List<String> specialRequirements;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDto {
        @NotNull(message = "Location type is required")
        private ProviderAvailability.Location.LocationType type;

        @Size(max = 500, message = "Address cannot exceed 500 characters")
        private String address;

        @Size(max = 50, message = "Room number cannot exceed 50 characters")
        private String roomNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PricingDto {
        @DecimalMin(value = "0.0", message = "Base fee cannot be negative")
        @DecimalMax(value = "10000.0", message = "Base fee cannot exceed 10000")
        private BigDecimal baseFee;

        private Boolean insuranceAccepted = false;

        @Size(max = 3, message = "Currency code cannot exceed 3 characters")
        private String currency = "USD";
    }
} 