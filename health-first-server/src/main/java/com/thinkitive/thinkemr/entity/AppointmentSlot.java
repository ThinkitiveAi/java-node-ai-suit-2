package com.thinkitive.thinkemr.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointment_slots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "availability_id", nullable = false)
    private ProviderAvailability availability;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @NotNull(message = "Slot start time is required")
    @Column(name = "slot_start_time", nullable = false)
    private LocalDateTime slotStartTime;

    @NotNull(message = "Slot end time is required")
    @Column(name = "slot_end_time", nullable = false)
    private LocalDateTime slotEndTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SlotStatus status = SlotStatus.AVAILABLE;

    @Column(name = "appointment_type", length = 50)
    private String appointmentType;

    @Column(name = "booking_reference", unique = true, length = 100)
    private String bookingReference;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum SlotStatus {
        AVAILABLE, BOOKED, CANCELLED, BLOCKED
    }

    // Helper method to generate booking reference
    public void generateBookingReference() {
        this.bookingReference = "APT-" + this.provider.getId().toString().substring(0, 8) + 
                               "-" + this.slotStartTime.toLocalDate() + 
                               "-" + this.slotStartTime.toLocalTime().toString().replace(":", "");
    }
} 