package com.thinkitive.thinkemr.dto;

import com.thinkitive.thinkemr.entity.Appointment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentListRequest {

    // Pagination
    private Integer page = 0;
    private Integer size = 10;

    // Date range filtering
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    // Filtering
    private String appointmentType;
    private String patientName;
    private String providerName;
    private Appointment.AppointmentStatus status;
    private String searchTerm; // General search across patient name, provider name, reason

    // Sorting
    private String sortBy = "dateTime"; // dateTime, patientName, providerName, status
    private String sortDirection = "desc"; // asc, desc

    // Additional filters
    private String patientId;
    private String providerId;
    private String bookingReference;
} 