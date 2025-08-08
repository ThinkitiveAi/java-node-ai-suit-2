package com.thinkitive.thinkemr.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private AppointmentSlot slot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_mode", nullable = false)
    private AppointmentMode appointmentMode;

    @NotBlank(message = "Appointment type is required")
    @Column(name = "appointment_type", nullable = false, length = 50)
    private String appointmentType;

    @NotNull(message = "Estimated amount is required")
    @DecimalMin(value = "0.0", message = "Estimated amount cannot be negative")
    @DecimalMax(value = "10000.0", message = "Estimated amount cannot exceed 10000")
    @Column(name = "estimated_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal estimatedAmount;

    @NotNull(message = "Date time is required")
    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Size(max = 1000, message = "Reason cannot exceed 1000 characters")
    @Column(name = "reason", length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @NotBlank(message = "Booking reference is required")
    @Column(name = "booking_reference", unique = true, nullable = false, length = 100)
    private String bookingReference;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum AppointmentMode {
        IN_PERSON, VIDEO_CALL, HOME
    }

    public enum AppointmentStatus {
        PENDING, CONFIRMED, CANCELLED
    }

    // Helper method to generate booking reference
    public void generateBookingReference() {
        this.bookingReference = "APT-" + this.provider.getId().toString().substring(0, 8) + 
                               "-" + this.patient.getId().toString().substring(0, 8) + 
                               "-" + this.dateTime.toLocalDate() + 
                               "-" + this.dateTime.toLocalTime().toString().replace(":", "");
    }
} 