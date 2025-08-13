package com.thinkitive.thinkemr.dto;

import com.thinkitive.thinkemr.entity.Appointment;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentBookingRequest {

    @NotNull(message = "Slot ID is required")
    private UUID slotId;

    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    @NotNull(message = "Appointment mode is required")
    private Appointment.AppointmentMode appointmentMode;

    @NotBlank(message = "Appointment type is required")
    @Size(max = 50, message = "Appointment type cannot exceed 50 characters")
    private String appointmentType;

    @NotNull(message = "Estimated amount is required")
    @DecimalMin(value = "0.0", message = "Estimated amount cannot be negative")
    @DecimalMax(value = "10000.0", message = "Estimated amount cannot exceed 10000")
    private BigDecimal estimatedAmount;

    @Size(max = 1000, message = "Reason cannot exceed 1000 characters")
    private String reason;
} 