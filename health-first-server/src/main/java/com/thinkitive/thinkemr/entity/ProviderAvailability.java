package com.thinkitive.thinkemr.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "provider_availability")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @NotNull(message = "Date is required")
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull(message = "Start time is required")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @NotBlank(message = "Timezone is required")
    @Column(name = "timezone", nullable = false, length = 50)
    private String timezone;

    @Column(name = "is_recurring", nullable = false)
    private Boolean isRecurring = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_pattern", length = 20)
    private RecurrencePattern recurrencePattern;

    @Column(name = "recurrence_end_date")
    private LocalDate recurrenceEndDate;

    @Min(value = 15, message = "Slot duration must be at least 15 minutes")
    @Max(value = 480, message = "Slot duration cannot exceed 8 hours")
    @Column(name = "slot_duration", nullable = false)
    private Integer slotDuration = 30; // minutes

    @Min(value = 0, message = "Break duration cannot be negative")
    @Max(value = 120, message = "Break duration cannot exceed 2 hours")
    @Column(name = "break_duration", nullable = false)
    private Integer breakDuration = 0; // minutes

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AvailabilityStatus status = AvailabilityStatus.AVAILABLE;

    @Min(value = 1, message = "Max appointments per slot must be at least 1")
    @Max(value = 10, message = "Max appointments per slot cannot exceed 10")
    @Column(name = "max_appointments_per_slot", nullable = false)
    private Integer maxAppointmentsPerSlot = 1;

    @Min(value = 0, message = "Current appointments cannot be negative")
    @Column(name = "current_appointments", nullable = false)
    private Integer currentAppointments = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_type", nullable = false)
    private AppointmentType appointmentType = AppointmentType.CONSULTATION;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "type", column = @Column(name = "location_type", nullable = false)),
        @AttributeOverride(name = "address", column = @Column(name = "location_address", length = 500)),
        @AttributeOverride(name = "roomNumber", column = @Column(name = "location_room_number", length = 50))
    })
    private Location location;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "baseFee", column = @Column(name = "pricing_base_fee", precision = 10, scale = 2)),
        @AttributeOverride(name = "insuranceAccepted", column = @Column(name = "pricing_insurance_accepted")),
        @AttributeOverride(name = "currency", column = @Column(name = "pricing_currency", length = 3))
    })
    private Pricing pricing;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    @Column(name = "notes", length = 500)
    private String notes;

    @ElementCollection
    @CollectionTable(name = "provider_availability_special_requirements", 
                    joinColumns = @JoinColumn(name = "availability_id"))
    @Column(name = "requirement", length = 100)
    private List<String> specialRequirements;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum RecurrencePattern {
        DAILY, WEEKLY, MONTHLY
    }

    public enum AvailabilityStatus {
        AVAILABLE, BOOKED, CANCELLED, BLOCKED, MAINTENANCE
    }

    public enum AppointmentType {
        CONSULTATION, FOLLOW_UP, EMERGENCY, TELEMEDICINE
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {
        @Enumerated(EnumType.STRING)
        @Column(name = "type", nullable = false)
        private LocationType type;

        @Size(max = 500, message = "Address cannot exceed 500 characters")
        private String address;

        @Size(max = 50, message = "Room number cannot exceed 50 characters")
        private String roomNumber;

        public enum LocationType {
            CLINIC, HOSPITAL, TELEMEDICINE, HOME_VISIT
        }
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pricing {
        @DecimalMin(value = "0.0", message = "Base fee cannot be negative")
        @DecimalMax(value = "10000.0", message = "Base fee cannot exceed 10000")
        private BigDecimal baseFee;

        private Boolean insuranceAccepted = false;

        @Size(max = 3, message = "Currency code cannot exceed 3 characters")
        private String currency = "USD";
    }
} 