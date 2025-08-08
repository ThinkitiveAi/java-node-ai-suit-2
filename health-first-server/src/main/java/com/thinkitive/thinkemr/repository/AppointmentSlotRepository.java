package com.thinkitive.thinkemr.repository;

import com.thinkitive.thinkemr.entity.AppointmentSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, UUID> {

    List<AppointmentSlot> findByProviderId(UUID providerId);

    List<AppointmentSlot> findByPatientId(UUID patientId);

    List<AppointmentSlot> findByProviderIdAndSlotStartTimeBetween(UUID providerId, LocalDateTime startTime, LocalDateTime endTime);

    List<AppointmentSlot> findByProviderIdAndStatus(UUID providerId, AppointmentSlot.SlotStatus status);

    Optional<AppointmentSlot> findByBookingReference(String bookingReference);

    @Query("SELECT as FROM AppointmentSlot as WHERE as.provider.id = :providerId " +
           "AND as.slotStartTime BETWEEN :startTime AND :endTime " +
           "AND (:status IS NULL OR as.status = :status)")
    List<AppointmentSlot> findByProviderIdAndTimeRangeAndStatus(
        @Param("providerId") UUID providerId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("status") AppointmentSlot.SlotStatus status
    );

    @Query("SELECT as FROM AppointmentSlot as WHERE as.provider.specialization = :specialization " +
           "AND as.slotStartTime BETWEEN :startTime AND :endTime " +
           "AND as.status = 'AVAILABLE' " +
           "AND (:appointmentType IS NULL OR as.appointmentType = :appointmentType)")
    List<AppointmentSlot> findAvailableBySpecializationAndTimeRange(
        @Param("specialization") String specialization,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("appointmentType") String appointmentType
    );

    @Query("SELECT COUNT(as) FROM AppointmentSlot as WHERE as.provider.id = :providerId " +
           "AND as.slotStartTime BETWEEN :startTime AND :endTime " +
           "AND as.status = 'BOOKED'")
    long countBookedSlotsByProviderAndTimeRange(
        @Param("providerId") UUID providerId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
} 