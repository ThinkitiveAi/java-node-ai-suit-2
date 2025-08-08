package com.thinkitive.thinkemr.service;

import com.thinkitive.thinkemr.dto.AppointmentBookingRequest;
import com.thinkitive.thinkemr.dto.AppointmentBookingResponse;
import com.thinkitive.thinkemr.dto.AvailableSlotsResponse;
import com.thinkitive.thinkemr.entity.Appointment;
import com.thinkitive.thinkemr.entity.AppointmentSlot;
import com.thinkitive.thinkemr.entity.Patient;
import com.thinkitive.thinkemr.entity.Provider;
import com.thinkitive.thinkemr.exception.ValidationException;
import com.thinkitive.thinkemr.repository.AppointmentRepository;
import com.thinkitive.thinkemr.repository.AppointmentSlotRepository;
import com.thinkitive.thinkemr.repository.PatientRepository;
import com.thinkitive.thinkemr.repository.ProviderRepository;
import com.thinkitive.thinkemr.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentSlotRepository slotRepository;
    private final PatientRepository patientRepository;
    private final ProviderRepository providerRepository;
    private final SecurityUtil securityUtil;

    @Transactional
    public AppointmentBookingResponse bookAppointment(AppointmentBookingRequest request) {
        log.info("Booking appointment for slot: {} and patient: {}", request.getSlotId(), request.getPatientId());

        // Validate slot exists and is available
        AppointmentSlot slot = slotRepository.findById(request.getSlotId())
            .orElseThrow(() -> new ValidationException("Slot not found"));

        if (slot.getStatus() != AppointmentSlot.SlotStatus.AVAILABLE) {
            throw new ValidationException("Slot is not available for booking");
        }

        // Validate provider and patient exist
        Provider provider = providerRepository.findById(slot.getProvider().getId())
            .orElseThrow(() -> new ValidationException("Provider not found"));

        Patient patient = patientRepository.findById(request.getPatientId())
            .orElseThrow(() -> new ValidationException("Patient not found"));

        // Check if slot has capacity
        long currentAppointments = appointmentRepository.countActiveAppointmentsBySlotId(slot.getId());
        if (currentAppointments >= slot.getAvailability().getMaxAppointmentsPerSlot()) {
            throw new ValidationException("Slot is at maximum capacity");
        }

        // Check for overlapping appointments for the same patient
        List<Appointment> overlappingAppointments = appointmentRepository.findOverlappingAppointments(
            request.getPatientId(), slot.getSlotStartTime());
        
        if (!overlappingAppointments.isEmpty()) {
            throw new ValidationException("Patient has overlapping appointment at this time");
        }

        // Create appointment
        Appointment appointment = new Appointment();
        appointment.setSlot(slot);
        appointment.setProvider(provider);
        appointment.setPatient(patient);
        appointment.setAppointmentMode(request.getAppointmentMode());
        appointment.setAppointmentType(request.getAppointmentType());
        appointment.setEstimatedAmount(request.getEstimatedAmount());
        appointment.setDateTime(slot.getSlotStartTime());
        appointment.setReason(request.getReason());
        appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);
        appointment.generateBookingReference();

        // Save appointment
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Update slot status if needed
        updateSlotStatus(slot);

        log.info("Appointment booked successfully with reference: {}", savedAppointment.getBookingReference());

        return AppointmentBookingResponse.fromAppointment(savedAppointment);
    }

    @Transactional
    public void cancelAppointment(UUID appointmentId, String reason) {
        log.info("Cancelling appointment: {} with reason: {}", appointmentId, reason);

        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new ValidationException("Appointment not found"));

        // Validate that the current user can cancel this appointment
        UUID currentPatientId = securityUtil.getCurrentPatientId();
        UUID currentProviderId = securityUtil.getCurrentProviderId();

        if (currentPatientId != null && !appointment.getPatient().getId().equals(currentPatientId)) {
            throw new ValidationException("Not authorized to cancel this appointment");
        }

        if (currentProviderId != null && !appointment.getProvider().getId().equals(currentProviderId)) {
            throw new ValidationException("Not authorized to cancel this appointment");
        }

        if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED) {
            throw new ValidationException("Appointment is already cancelled");
        }

        // Cancel appointment
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        // Update slot status
        AppointmentSlot slot = appointment.getSlot();
        updateSlotStatus(slot);

        log.info("Appointment cancelled successfully: {}", appointmentId);
    }

    @Transactional(readOnly = true)
    public AvailableSlotsResponse getAvailableSlots(UUID providerId, LocalDate startDate, LocalDate endDate, 
                                                   String appointmentType, String timezone) {
        log.info("Getting available slots for provider: {} from {} to {}", providerId, startDate, endDate);

        // Validate provider exists
        providerRepository.findById(providerId)
            .orElseThrow(() -> new ValidationException("Provider not found"));

        // Get available slots
        List<AppointmentSlot> availableSlots = slotRepository
            .findByProviderIdAndTimeRangeAndStatus(
                providerId,
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX),
                AppointmentSlot.SlotStatus.AVAILABLE
            );

        // Filter by appointment type if specified
        if (appointmentType != null) {
            availableSlots = availableSlots.stream()
                .filter(slot -> appointmentType.equals(slot.getAppointmentType()))
                .toList();
        }

        // Filter slots that have capacity
        availableSlots = availableSlots.stream()
            .filter(slot -> {
                long currentAppointments = appointmentRepository.countActiveAppointmentsBySlotId(slot.getId());
                return currentAppointments < slot.getAvailability().getMaxAppointmentsPerSlot();
            })
            .toList();

        return AvailableSlotsResponse.fromSlots(providerId, availableSlots);
    }

    @Transactional(readOnly = true)
    public List<AppointmentBookingResponse> getPatientAppointments(UUID patientId) {
        log.info("Getting appointments for patient: {}", patientId);

        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        return appointments.stream()
            .map(AppointmentBookingResponse::fromAppointment)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentBookingResponse> getProviderAppointments(UUID providerId) {
        log.info("Getting appointments for provider: {}", providerId);

        List<Appointment> appointments = appointmentRepository.findByProviderId(providerId);
        return appointments.stream()
            .map(AppointmentBookingResponse::fromAppointment)
            .toList();
    }

    @Transactional(readOnly = true)
    public AppointmentBookingResponse getAppointmentByReference(String bookingReference) {
        log.info("Getting appointment by reference: {}", bookingReference);

        Appointment appointment = appointmentRepository.findByBookingReference(bookingReference)
            .orElseThrow(() -> new ValidationException("Appointment not found"));

        return AppointmentBookingResponse.fromAppointment(appointment);
    }

    @Transactional
    public void confirmAppointment(UUID appointmentId) {
        log.info("Confirming appointment: {}", appointmentId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new ValidationException("Appointment not found"));

        // Validate that the current provider can confirm this appointment
        UUID currentProviderId = securityUtil.getCurrentProviderId();
        if (currentProviderId == null || !appointment.getProvider().getId().equals(currentProviderId)) {
            throw new ValidationException("Not authorized to confirm this appointment");
        }

        if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING) {
            throw new ValidationException("Appointment is not in pending status");
        }

        appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appointment);

        log.info("Appointment confirmed successfully: {}", appointmentId);
    }

    private void updateSlotStatus(AppointmentSlot slot) {
        long currentAppointments = appointmentRepository.countActiveAppointmentsBySlotId(slot.getId());
        int maxAppointments = slot.getAvailability().getMaxAppointmentsPerSlot();

        if (currentAppointments >= maxAppointments) {
            slot.setStatus(AppointmentSlot.SlotStatus.BOOKED);
        } else if (currentAppointments == 0) {
            slot.setStatus(AppointmentSlot.SlotStatus.AVAILABLE);
        }

        slotRepository.save(slot);
    }
} 