package com.thinkitive.thinkemr.controller;

import com.thinkitive.thinkemr.dto.ApiResponse;
import com.thinkitive.thinkemr.dto.AppointmentBookingRequest;
import com.thinkitive.thinkemr.dto.AppointmentBookingResponse;
import com.thinkitive.thinkemr.dto.AvailableSlotsResponse;
import com.thinkitive.thinkemr.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentBookingResponse>> bookAppointment(
            @Valid @RequestBody AppointmentBookingRequest request) {
        
        log.info("Received appointment booking request for slot: {} and patient: {}", 
                request.getSlotId(), request.getPatientId());
        
        try {
            AppointmentBookingResponse response = appointmentService.bookAppointment(request);
            
            ApiResponse<AppointmentBookingResponse> apiResponse = ApiResponse.success(
                "Appointment booked successfully",
                response
            );
            
            log.info("Appointment booking successful with reference: {}", response.getBookingReference());
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
            
        } catch (Exception e) {
            log.error("Error during appointment booking", e);
            throw e;
        }
    }

    @GetMapping("/providers/{providerId}/slots")
    public ResponseEntity<ApiResponse<AvailableSlotsResponse>> getAvailableSlots(
            @PathVariable UUID providerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String appointmentType,
            @RequestParam(required = false) String timezone) {
        
        log.info("Getting available slots for provider: {} from {} to {}", providerId, startDate, endDate);
        
        try {
            AvailableSlotsResponse response = appointmentService.getAvailableSlots(
                providerId, startDate, endDate, appointmentType, timezone);
            
            ApiResponse<AvailableSlotsResponse> apiResponse = ApiResponse.success(
                "Available slots retrieved successfully",
                response
            );
            
            log.info("Available slots retrieval successful for provider: {}", providerId);
            return ResponseEntity.ok(apiResponse);
            
        } catch (Exception e) {
            log.error("Error during available slots retrieval", e);
            throw e;
        }
    }

    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<ApiResponse<Void>> cancelAppointment(
            @PathVariable UUID appointmentId,
            @RequestParam(required = false) String reason) {
        
        log.info("Cancelling appointment: {} with reason: {}", appointmentId, reason);
        
        try {
            appointmentService.cancelAppointment(appointmentId, reason);
            
            ApiResponse<Void> apiResponse = ApiResponse.success(
                "Appointment cancelled successfully",
                null
            );
            
            log.info("Appointment cancellation successful: {}", appointmentId);
            return ResponseEntity.ok(apiResponse);
            
        } catch (Exception e) {
            log.error("Error during appointment cancellation: {}", appointmentId, e);
            throw e;
        }
    }

    @GetMapping("/patients/{patientId}")
    public ResponseEntity<ApiResponse<List<AppointmentBookingResponse>>> getPatientAppointments(
            @PathVariable UUID patientId) {
        
        log.info("Getting appointments for patient: {}", patientId);
        
        try {
            List<AppointmentBookingResponse> appointments = appointmentService.getPatientAppointments(patientId);
            
            ApiResponse<List<AppointmentBookingResponse>> apiResponse = ApiResponse.success(
                "Patient appointments retrieved successfully",
                appointments
            );
            
            log.info("Patient appointments retrieval successful for patient: {}", patientId);
            return ResponseEntity.ok(apiResponse);
            
        } catch (Exception e) {
            log.error("Error during patient appointments retrieval", e);
            throw e;
        }
    }

    @GetMapping("/providers/{providerId}")
    public ResponseEntity<ApiResponse<List<AppointmentBookingResponse>>> getProviderAppointments(
            @PathVariable UUID providerId) {
        
        log.info("Getting appointments for provider: {}", providerId);
        
        try {
            List<AppointmentBookingResponse> appointments = appointmentService.getProviderAppointments(providerId);
            
            ApiResponse<List<AppointmentBookingResponse>> apiResponse = ApiResponse.success(
                "Provider appointments retrieved successfully",
                appointments
            );
            
            log.info("Provider appointments retrieval successful for provider: {}", providerId);
            return ResponseEntity.ok(apiResponse);
            
        } catch (Exception e) {
            log.error("Error during provider appointments retrieval", e);
            throw e;
        }
    }

    @GetMapping("/reference/{bookingReference}")
    public ResponseEntity<ApiResponse<AppointmentBookingResponse>> getAppointmentByReference(
            @PathVariable String bookingReference) {
        
        log.info("Getting appointment by reference: {}", bookingReference);
        
        try {
            AppointmentBookingResponse appointment = appointmentService.getAppointmentByReference(bookingReference);
            
            ApiResponse<AppointmentBookingResponse> apiResponse = ApiResponse.success(
                "Appointment retrieved successfully",
                appointment
            );
            
            log.info("Appointment retrieval successful by reference: {}", bookingReference);
            return ResponseEntity.ok(apiResponse);
            
        } catch (Exception e) {
            log.error("Error during appointment retrieval by reference: {}", bookingReference, e);
            throw e;
        }
    }

    @PostMapping("/{appointmentId}/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmAppointment(@PathVariable UUID appointmentId) {
        
        log.info("Confirming appointment: {}", appointmentId);
        
        try {
            appointmentService.confirmAppointment(appointmentId);
            
            ApiResponse<Void> apiResponse = ApiResponse.success(
                "Appointment confirmed successfully",
                null
            );
            
            log.info("Appointment confirmation successful: {}", appointmentId);
            return ResponseEntity.ok(apiResponse);
            
        } catch (Exception e) {
            log.error("Error during appointment confirmation: {}", appointmentId, e);
            throw e;
        }
    }
} 