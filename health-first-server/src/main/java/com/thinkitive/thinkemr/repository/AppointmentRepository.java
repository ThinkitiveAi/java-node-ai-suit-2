package com.thinkitive.thinkemr.repository;

import com.thinkitive.thinkemr.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    // Comprehensive appointment listing with filters and pagination
    @Query("SELECT a FROM Appointment a WHERE " +
           "(:startDate IS NULL OR DATE(a.dateTime) >= :startDate) AND " +
           "(:endDate IS NULL OR DATE(a.dateTime) <= :endDate) AND " +
           "(:appointmentType IS NULL OR a.appointmentType = :appointmentType) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:patientName IS NULL OR (LOWER(a.patient.firstName) LIKE LOWER(CONCAT('%', :patientName, '%')) OR " +
           "LOWER(a.patient.lastName) LIKE LOWER(CONCAT('%', :patientName, '%')))) AND " +
           "(:providerName IS NULL OR (LOWER(a.provider.firstName) LIKE LOWER(CONCAT('%', :providerName, '%')) OR " +
           "LOWER(a.provider.lastName) LIKE LOWER(CONCAT('%', :providerName, '%')))) AND " +
           "(:searchTerm IS NULL OR " +
           "LOWER(a.patient.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.patient.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.provider.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.provider.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.reason) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.bookingReference) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Appointment> findAppointmentsWithFilters(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("appointmentType") String appointmentType,
        @Param("status") Appointment.AppointmentStatus status,
        @Param("patientName") String patientName,
        @Param("providerName") String providerName,
        @Param("searchTerm") String searchTerm,
        Pageable pageable
    );

    // Count appointments by status for summary
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = :status")
    long countByStatus(@Param("status") Appointment.AppointmentStatus status);

    // Count total appointments
    @Query("SELECT COUNT(a) FROM Appointment a")
    long countTotalAppointments();

    // Find appointments for a specific provider with filters
    @Query("SELECT a FROM Appointment a WHERE a.provider.id = :providerId AND " +
           "(:startDate IS NULL OR DATE(a.dateTime) >= :startDate) AND " +
           "(:endDate IS NULL OR DATE(a.dateTime) <= :endDate) AND " +
           "(:appointmentType IS NULL OR a.appointmentType = :appointmentType) AND " +
           "(:status IS NULL OR a.status = :status)")
    Page<Appointment> findProviderAppointmentsWithFilters(
        @Param("providerId") UUID providerId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("appointmentType") String appointmentType,
        @Param("status") Appointment.AppointmentStatus status,
        Pageable pageable
    );

    // Find appointments for a specific patient with filters
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND " +
           "(:startDate IS NULL OR DATE(a.dateTime) >= :startDate) AND " +
           "(:endDate IS NULL OR DATE(a.dateTime) <= :endDate) AND " +
           "(:appointmentType IS NULL OR a.appointmentType = :appointmentType) AND " +
           "(:status IS NULL OR a.status = :status)")
    Page<Appointment> findPatientAppointmentsWithFilters(
        @Param("patientId") UUID patientId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("appointmentType") String appointmentType,
        @Param("status") Appointment.AppointmentStatus status,
        Pageable pageable
    );
} 