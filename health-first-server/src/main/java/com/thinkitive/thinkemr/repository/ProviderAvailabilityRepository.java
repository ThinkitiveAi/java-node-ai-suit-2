package com.thinkitive.thinkemr.repository;

import com.thinkitive.thinkemr.entity.ProviderAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProviderAvailabilityRepository extends JpaRepository<ProviderAvailability, UUID> {

    List<ProviderAvailability> findByProviderId(UUID providerId);

    List<ProviderAvailability> findByProviderIdAndDateBetween(UUID providerId, LocalDate startDate, LocalDate endDate);

    List<ProviderAvailability> findByProviderIdAndStatus(UUID providerId, ProviderAvailability.AvailabilityStatus status);

    @Query("SELECT pa FROM ProviderAvailability pa WHERE pa.provider.id = :providerId " +
           "AND pa.date BETWEEN :startDate AND :endDate " +
           "AND (:status IS NULL OR pa.status = :status) " +
           "AND (:appointmentType IS NULL OR pa.appointmentType = :appointmentType)")
    List<ProviderAvailability> findByProviderIdAndDateRangeAndFilters(
        @Param("providerId") UUID providerId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("status") ProviderAvailability.AvailabilityStatus status,
        @Param("appointmentType") ProviderAvailability.AppointmentType appointmentType
    );

    @Query("SELECT pa FROM ProviderAvailability pa WHERE pa.provider.specialization = :specialization " +
           "AND pa.date BETWEEN :startDate AND :endDate " +
           "AND pa.status = 'AVAILABLE' " +
           "AND (:appointmentType IS NULL OR pa.appointmentType = :appointmentType) " +
           "AND (:locationType IS NULL OR pa.location.type = :locationType)")
    List<ProviderAvailability> findAvailableBySpecializationAndDateRange(
        @Param("specialization") String specialization,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("appointmentType") ProviderAvailability.AppointmentType appointmentType,
        @Param("locationType") ProviderAvailability.Location.LocationType locationType
    );
} 