package com.thinkitive.thinkemr.repository;

import com.thinkitive.thinkemr.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByPatientId(UUID patientId);

    List<Appointment> findByProviderId(UUID providerId);

    List<Appointment> findBySlotId(UUID slotId);

    List<Appointment> findByStatus(Appointment.AppointmentStatus status);

    Optional<Appointment> findByBookingReference(String bookingReference);

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.dateTime BETWEEN :startTime AND :endTime")
    List<Appointment> findByPatientIdAndDateTimeBetween(
        @Param("patientId") UUID patientId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT a FROM Appointment a WHERE a.provider.id = :providerId AND a.dateTime BETWEEN :startTime AND :endTime")
    List<Appointment> findByProviderIdAndDateTimeBetween(
        @Param("providerId") UUID providerId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.status = :status")
    List<Appointment> findByPatientIdAndStatus(
        @Param("patientId") UUID patientId,
        @Param("status") Appointment.AppointmentStatus status
    );

    @Query("SELECT a FROM Appointment a WHERE a.provider.id = :providerId AND a.status = :status")
    List<Appointment> findByProviderIdAndStatus(
        @Param("providerId") UUID providerId,
        @Param("status") Appointment.AppointmentStatus status
    );

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.slot.id = :slotId AND a.status IN ('PENDING', 'CONFIRMED')")
    long countActiveAppointmentsBySlotId(@Param("slotId") UUID slotId);

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.dateTime = :dateTime AND a.status IN ('PENDING', 'CONFIRMED')")
    List<Appointment> findOverlappingAppointments(
        @Param("patientId") UUID patientId,
        @Param("dateTime") LocalDateTime dateTime
    );
} 