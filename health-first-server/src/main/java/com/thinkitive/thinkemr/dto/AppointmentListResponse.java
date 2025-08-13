package com.thinkitive.thinkemr.dto;

import com.thinkitive.thinkemr.entity.Appointment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentListResponse {

    private List<AppointmentDto> appointments;
    private PaginationInfo pagination;
    private FilterCriteria filters;
    private SummaryInfo summary;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppointmentDto {
        private UUID appointmentId;
        private String bookingReference;
        private LocalDateTime dateTime;
        private String appointmentType;
        private PatientInfo patient;
        private ProviderInfo provider;
        private String reason;
        private Appointment.AppointmentStatus status;
        private Appointment.AppointmentMode appointmentMode;
        private BigDecimal estimatedAmount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientInfo {
        private UUID patientId;
        private String firstName;
        private String lastName;
        private String fullName;
        private String gender;
        private LocalDate dateOfBirth;
        private Integer age;
        private String phoneNumber;
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProviderInfo {
        private UUID providerId;
        private String firstName;
        private String lastName;
        private String fullName;
        private String specialization;
        private String licenseNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo {
        private Integer currentPage;
        private Integer totalPages;
        private Integer pageSize;
        private Long totalElements;
        private Boolean hasNext;
        private Boolean hasPrevious;
        private String showingText; // "Showing 1 to 11 of 100 entries"
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FilterCriteria {
        private LocalDate startDate;
        private LocalDate endDate;
        private String appointmentType;
        private String patientName;
        private String providerName;
        private Appointment.AppointmentStatus status;
        private String searchTerm;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryInfo {
        private Long totalAppointments;
        private Long scheduledAppointments;
        private Long confirmedAppointments;
        private Long cancelledAppointments;
        private Long pendingAppointments;
    }

    public static AppointmentDto fromAppointment(Appointment appointment) {
        // Calculate age
        Integer age = null;
        if (appointment.getPatient().getDateOfBirth() != null) {
            age = LocalDate.now().getYear() - appointment.getPatient().getDateOfBirth().getYear();
        }

        // Build patient info
        PatientInfo patientInfo = new PatientInfo(
            appointment.getPatient().getId(),
            appointment.getPatient().getFirstName(),
            appointment.getPatient().getLastName(),
            appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName(),
            appointment.getPatient().getGender().toString(),
            appointment.getPatient().getDateOfBirth(),
            age,
            appointment.getPatient().getPhoneNumber(),
            appointment.getPatient().getEmail()
        );

        // Build provider info
        ProviderInfo providerInfo = new ProviderInfo(
            appointment.getProvider().getId(),
            appointment.getProvider().getFirstName(),
            appointment.getProvider().getLastName(),
            appointment.getProvider().getFirstName() + " " + appointment.getProvider().getLastName(),
            appointment.getProvider().getSpecialization(),
            appointment.getProvider().getLicenseNumber()
        );

        return new AppointmentDto(
            appointment.getId(),
            appointment.getBookingReference(),
            appointment.getDateTime(),
            appointment.getAppointmentType(),
            patientInfo,
            providerInfo,
            appointment.getReason(),
            appointment.getStatus(),
            appointment.getAppointmentMode(),
            appointment.getEstimatedAmount(),
            appointment.getCreatedAt(),
            appointment.getUpdatedAt()
        );
    }
} 