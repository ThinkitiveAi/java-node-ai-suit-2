package com.thinkitive.thinkemr.repository;

import com.thinkitive.thinkemr.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, UUID> {

    Optional<Provider> findByEmail(String email);

    Optional<Provider> findByPhoneNumber(String phoneNumber);

    Optional<Provider> findByLicenseNumber(String licenseNumber);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Provider p WHERE p.email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Provider p WHERE p.phoneNumber = :phoneNumber")
    boolean existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Provider p WHERE p.licenseNumber = :licenseNumber")
    boolean existsByLicenseNumber(@Param("licenseNumber") String licenseNumber);
} 