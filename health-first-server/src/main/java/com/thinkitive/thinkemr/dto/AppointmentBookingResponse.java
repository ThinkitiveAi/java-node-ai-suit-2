package com.thinkitive.thinkemr.dto;

import com.thinkitive.thinkemr.entity.Appointment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentBookingResponse {

    private UUID appointmentId;
    private String bookingReference;
    private UUID slotId;
    private UUID providerId;
    private UUID patientId;
    private LocalDateTime dateTime;
    private Appointment.AppointmentStatus status;
    private Appointment.AppointmentMode appointmentMode;
    private String appointmentType;
    private BigDecimal estimatedAmount;
    private String reason;

    public static AppointmentBookingResponse fromAppointment(Appointment appointment) {
        return new AppointmentBookingResponse(
            appointment.getId(),
            appointment.getBookingReference(),
            appointment.getSlot().getId(),
            appointment.getProvider().getId(),
            appointment.getPatient().getId(),
            appointment.getDateTime(),
            appointment.getStatus(),
            appointment.getAppointmentMode(),
            appointment.getAppointmentType(),
            appointment.getEstimatedAmount(),
            appointment.getReason()
        );
    }
} 