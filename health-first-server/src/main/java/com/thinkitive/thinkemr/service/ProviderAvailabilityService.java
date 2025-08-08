package com.thinkitive.thinkemr.service;

import com.thinkitive.thinkemr.dto.AvailabilitySearchRequest;
import com.thinkitive.thinkemr.dto.AvailabilitySearchResponse;
import com.thinkitive.thinkemr.dto.ProviderAvailabilityRequest;
import com.thinkitive.thinkemr.dto.ProviderAvailabilityResponse;
import com.thinkitive.thinkemr.entity.AppointmentSlot;
import com.thinkitive.thinkemr.entity.Patient;
import com.thinkitive.thinkemr.entity.Provider;
import com.thinkitive.thinkemr.entity.ProviderAvailability;
import com.thinkitive.thinkemr.exception.ValidationException;
import com.thinkitive.thinkemr.repository.AppointmentSlotRepository;
import com.thinkitive.thinkemr.repository.PatientRepository;
import com.thinkitive.thinkemr.repository.ProviderAvailabilityRepository;
import com.thinkitive.thinkemr.repository.ProviderRepository;
import com.thinkitive.thinkemr.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProviderAvailabilityService {

    private final ProviderAvailabilityRepository availabilityRepository;
    private final AppointmentSlotRepository slotRepository;
    private final ProviderRepository providerRepository;
    private final PatientRepository patientRepository;
    private final SecurityUtil securityUtil;

    @Transactional
    public ProviderAvailabilityResponse createAvailability(ProviderAvailabilityRequest request) {
        log.info("Creating availability for provider");

        // Get current provider
        UUID providerId = securityUtil.getCurrentProviderId();
        if (providerId == null) {
            throw new ValidationException("Provider not authenticated");
        }

        Provider provider = providerRepository.findById(providerId)
            .orElseThrow(() -> new ValidationException("Provider not found"));

        // Validate time range
        validateTimeRange(request.getStartTime(), request.getEndTime());

        // Check for conflicts
        checkForConflicts(providerId, request.getDate(), request.getStartTime(), request.getEndTime());

        // Create availability
        ProviderAvailability availability = new ProviderAvailability();
        availability.setProvider(provider);
        availability.setDate(request.getDate());
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());
        availability.setTimezone(request.getTimezone());
        availability.setIsRecurring(request.getIsRecurring());
        availability.setRecurrencePattern(request.getRecurrencePattern());
        availability.setRecurrenceEndDate(request.getRecurrenceEndDate());
        availability.setSlotDuration(request.getSlotDuration());
        availability.setBreakDuration(request.getBreakDuration());
        availability.setMaxAppointmentsPerSlot(request.getMaxAppointmentsPerSlot());
        availability.setAppointmentType(request.getAppointmentType());
        availability.setNotes(request.getNotes());
        availability.setSpecialRequirements(request.getSpecialRequirements());

        // Set location
        if (request.getLocation() != null) {
            availability.setLocation(new ProviderAvailability.Location(
                request.getLocation().getType(),
                request.getLocation().getAddress(),
                request.getLocation().getRoomNumber()
            ));
        }

        // Set pricing
        if (request.getPricing() != null) {
            availability.setPricing(new ProviderAvailability.Pricing(
                request.getPricing().getBaseFee(),
                request.getPricing().getInsuranceAccepted(),
                request.getPricing().getCurrency()
            ));
        }

        // Save availability
        ProviderAvailability savedAvailability = availabilityRepository.save(availability);

        // Generate appointment slots
        List<LocalDate> dates = generateDates(savedAvailability);
        List<AppointmentSlot> slots = generateAppointmentSlots(savedAvailability, dates);

        log.info("Created availability with {} slots", slots.size());

        // Create response
        ProviderAvailabilityResponse response = ProviderAvailabilityResponse.fromAvailability(savedAvailability);
        response.setSlotsCreated(slots.size());
        response.setDateRange(new ProviderAvailabilityResponse.DateRangeDto(
            dates.get(0), dates.get(dates.size() - 1)
        ));
        response.setTotalAppointmentsAvailable(slots.size() * savedAvailability.getMaxAppointmentsPerSlot());

        return response;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getProviderAvailability(UUID providerId, LocalDate startDate, LocalDate endDate,
                                                      ProviderAvailability.AvailabilityStatus status,
                                                      ProviderAvailability.AppointmentType appointmentType) {
        log.info("Getting availability for provider: {} from {} to {}", providerId, startDate, endDate);

        List<ProviderAvailability> availabilities = availabilityRepository
            .findByProviderIdAndDateRangeAndFilters(providerId, startDate, endDate, status, appointmentType);

        // Group by date
        Map<LocalDate, List<ProviderAvailability>> groupedByDate = availabilities.stream()
            .collect(Collectors.groupingBy(ProviderAvailability::getDate));

        // Calculate summary
        int totalSlots = 0;
        int availableSlots = 0;
        int bookedSlots = 0;
        int cancelledSlots = 0;

        List<Map<String, Object>> availabilityList = new ArrayList<>();
        for (Map.Entry<LocalDate, List<ProviderAvailability>> entry : groupedByDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<ProviderAvailability> dayAvailabilities = entry.getValue();

            List<Map<String, Object>> slots = new ArrayList<>();
            for (ProviderAvailability availability : dayAvailabilities) {
                // Get appointment slots for this availability
                List<AppointmentSlot> appointmentSlots = slotRepository
                    .findByProviderIdAndTimeRangeAndStatus(providerId,
                        availability.getDate().atTime(availability.getStartTime()),
                        availability.getDate().atTime(availability.getEndTime()),
                        null);

                for (AppointmentSlot slot : appointmentSlots) {
                    Map<String, Object> slotInfo = new HashMap<>();
                    slotInfo.put("slot_id", slot.getId());
                    slotInfo.put("start_time", slot.getSlotStartTime().toLocalTime());
                    slotInfo.put("end_time", slot.getSlotEndTime().toLocalTime());
                    slotInfo.put("status", slot.getStatus());
                    slotInfo.put("appointment_type", slot.getAppointmentType());

                    if (availability.getLocation() != null) {
                        Map<String, Object> location = new HashMap<>();
                        location.put("type", availability.getLocation().getType());
                        location.put("address", availability.getLocation().getAddress());
                        location.put("room_number", availability.getLocation().getRoomNumber());
                        slotInfo.put("location", location);
                    }

                    if (availability.getPricing() != null) {
                        Map<String, Object> pricing = new HashMap<>();
                        pricing.put("base_fee", availability.getPricing().getBaseFee());
                        pricing.put("insurance_accepted", availability.getPricing().getInsuranceAccepted());
                        slotInfo.put("pricing", pricing);
                    }

                    slots.add(slotInfo);

                    // Update counters
                    totalSlots++;
                    switch (slot.getStatus()) {
                        case AVAILABLE -> availableSlots++;
                        case BOOKED -> bookedSlots++;
                        case CANCELLED -> cancelledSlots++;
                    }
                }
            }

            Map<String, Object> dayAvailability = new HashMap<>();
            dayAvailability.put("date", date);
            dayAvailability.put("slots", slots);
            availabilityList.add(dayAvailability);
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("total_slots", totalSlots);
        summary.put("available_slots", availableSlots);
        summary.put("booked_slots", bookedSlots);
        summary.put("cancelled_slots", cancelledSlots);

        Map<String, Object> response = new HashMap<>();
        response.put("provider_id", providerId);
        response.put("availability_summary", summary);
        response.put("availability", availabilityList);

        return response;
    }

    @Transactional
    public void updateAvailabilitySlot(UUID slotId, ProviderAvailabilityRequest request) {
        log.info("Updating availability slot: {}", slotId);

        AppointmentSlot slot = slotRepository.findById(slotId)
            .orElseThrow(() -> new ValidationException("Slot not found"));

        // Validate provider ownership
        UUID currentProviderId = securityUtil.getCurrentProviderId();
        if (!slot.getProvider().getId().equals(currentProviderId)) {
            throw new ValidationException("Not authorized to update this slot");
        }

        // Update slot
        if (request.getStartTime() != null) {
            slot.setSlotStartTime(slot.getSlotStartTime().toLocalDate().atTime(request.getStartTime()));
        }
        if (request.getEndTime() != null) {
            slot.setSlotEndTime(slot.getSlotEndTime().toLocalDate().atTime(request.getEndTime()));
        }
        if (request.getNotes() != null) {
            // Update the availability notes
            ProviderAvailability availability = slot.getAvailability();
            availability.setNotes(request.getNotes());
            availabilityRepository.save(availability);
        }

        slotRepository.save(slot);
        log.info("Updated availability slot: {}", slotId);
    }

    @Transactional
    public void deleteAvailabilitySlot(UUID slotId, Boolean deleteRecurring, String reason) {
        log.info("Deleting availability slot: {} with recurring: {}", slotId, deleteRecurring);

        AppointmentSlot slot = slotRepository.findById(slotId)
            .orElseThrow(() -> new ValidationException("Slot not found"));

        // Validate provider ownership
        UUID currentProviderId = securityUtil.getCurrentProviderId();
        if (!slot.getProvider().getId().equals(currentProviderId)) {
            throw new ValidationException("Not authorized to delete this slot");
        }

        if (Boolean.TRUE.equals(deleteRecurring)) {
            // Delete all slots from the same availability
            List<AppointmentSlot> relatedSlots = slotRepository
                .findByProviderIdAndTimeRangeAndStatus(slot.getProvider().getId(),
                    slot.getSlotStartTime().toLocalDate().atStartOfDay(),
                    slot.getSlotStartTime().toLocalDate().atTime(LocalTime.MAX),
                    null);

            for (AppointmentSlot relatedSlot : relatedSlots) {
                if (relatedSlot.getAvailability().getId().equals(slot.getAvailability().getId())) {
                    slotRepository.delete(relatedSlot);
                }
            }
        } else {
            // Delete only this slot
            slotRepository.delete(slot);
        }

        log.info("Deleted availability slot: {} with reason: {}", slotId, reason);
    }

    @Transactional(readOnly = true)
    public AvailabilitySearchResponse searchAvailability(AvailabilitySearchRequest request) {
        log.info("Searching availability with criteria: {}", request);

        // Determine date range
        LocalDate startDate = request.getDate() != null ? request.getDate() : request.getStartDate();
        LocalDate endDate = request.getDate() != null ? request.getDate() : request.getEndDate();

        if (startDate == null || endDate == null) {
            throw new ValidationException("Date range is required");
        }

        // Search for available slots
        List<AppointmentSlot> availableSlots = slotRepository
            .findAvailableBySpecializationAndTimeRange(
                request.getSpecialization(),
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX),
                request.getAppointmentType() != null ? request.getAppointmentType().toString() : null
            );

        // Filter by additional criteria
        List<AppointmentSlot> filteredSlots = availableSlots.stream()
            .filter(slot -> filterByLocation(slot, request.getLocation()))
            .filter(slot -> filterByInsurance(slot, request.getInsuranceAccepted()))
            .filter(slot -> filterByPrice(slot, request.getMaxPrice()))
            .collect(Collectors.toList());

        // Group by provider
        Map<Provider, List<AppointmentSlot>> groupedByProvider = filteredSlots.stream()
            .collect(Collectors.groupingBy(AppointmentSlot::getProvider));

        // Build response
        List<AvailabilitySearchResponse.SearchResultDto> results = new ArrayList<>();
        for (Map.Entry<Provider, List<AppointmentSlot>> entry : groupedByProvider.entrySet()) {
            Provider provider = entry.getKey();
            List<AppointmentSlot> providerSlots = entry.getValue();

            AvailabilitySearchResponse.SearchResultDto result = new AvailabilitySearchResponse.SearchResultDto();
            
            // Provider info
            AvailabilitySearchResponse.ProviderDto providerDto = new AvailabilitySearchResponse.ProviderDto();
            providerDto.setId(provider.getId());
            providerDto.setName(provider.getFirstName() + " " + provider.getLastName());
            providerDto.setSpecialization(provider.getSpecialization());
            providerDto.setYearsOfExperience(provider.getYearsOfExperience());
            providerDto.setClinicAddress(provider.getClinicAddress().getStreet() + ", " + 
                                      provider.getClinicAddress().getCity() + ", " + 
                                      provider.getClinicAddress().getState());
            result.setProvider(providerDto);

            // Available slots
            List<AvailabilitySearchResponse.AvailableSlotDto> availableSlotDtos = providerSlots.stream()
                .map(this::convertToAvailableSlotDto)
                .collect(Collectors.toList());
            result.setAvailableSlots(availableSlotDtos);

            results.add(result);
        }

        AvailabilitySearchResponse response = new AvailabilitySearchResponse();
        response.setSearchCriteria(convertToSearchCriteria(request));
        response.setTotalResults(results.size());
        response.setResults(results);

        return response;
    }

    private void validateTimeRange(LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new ValidationException("Start time must be before end time");
        }
        if (startTime.equals(endTime)) {
            throw new ValidationException("Start time and end time cannot be the same");
        }
    }

    private void checkForConflicts(UUID providerId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<AppointmentSlot> existingSlots = slotRepository
            .findByProviderIdAndSlotStartTimeBetween(providerId,
                date.atStartOfDay(),
                date.atTime(LocalTime.MAX));

        for (AppointmentSlot slot : existingSlots) {
            if (hasTimeOverlap(startTime, endTime, 
                slot.getSlotStartTime().toLocalTime(), slot.getSlotEndTime().toLocalTime())) {
                throw new ValidationException("Time slot conflicts with existing availability");
            }
        }
    }

    private boolean hasTimeOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }

    private List<LocalDate> generateDates(ProviderAvailability availability) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = availability.getDate();

        if (Boolean.TRUE.equals(availability.getIsRecurring()) && availability.getRecurrencePattern() != null) {
            LocalDate endDate = availability.getRecurrenceEndDate() != null ? 
                availability.getRecurrenceEndDate() : currentDate.plusMonths(6);

            while (!currentDate.isAfter(endDate)) {
                dates.add(currentDate);
                currentDate = getNextRecurrenceDate(currentDate, availability.getRecurrencePattern());
            }
        } else {
            dates.add(currentDate);
        }

        return dates;
    }

    private LocalDate getNextRecurrenceDate(LocalDate currentDate, ProviderAvailability.RecurrencePattern pattern) {
        return switch (pattern) {
            case DAILY -> currentDate.plusDays(1);
            case WEEKLY -> currentDate.plusWeeks(1);
            case MONTHLY -> currentDate.plusMonths(1);
        };
    }

    private List<AppointmentSlot> generateAppointmentSlots(ProviderAvailability availability, List<LocalDate> dates) {
        List<AppointmentSlot> slots = new ArrayList<>();

        for (LocalDate date : dates) {
            LocalTime currentTime = availability.getStartTime();
            LocalTime endTime = availability.getEndTime();

            while (currentTime.isBefore(endTime)) {
                LocalTime slotEndTime = currentTime.plusMinutes(availability.getSlotDuration());

                if (slotEndTime.isAfter(endTime)) {
                    break;
                }

                AppointmentSlot slot = new AppointmentSlot();
                slot.setAvailability(availability);
                slot.setProvider(availability.getProvider());
                slot.setSlotStartTime(date.atTime(currentTime));
                slot.setSlotEndTime(date.atTime(slotEndTime));
                slot.setAppointmentType(availability.getAppointmentType().toString());
                slot.generateBookingReference();

                slots.add(slot);

                // Add break duration
                currentTime = slotEndTime.plusMinutes(availability.getBreakDuration());
            }
        }

        return slotRepository.saveAll(slots);
    }

    private boolean filterByLocation(AppointmentSlot slot, String location) {
        if (location == null) return true;
        
        ProviderAvailability.Location slotLocation = slot.getAvailability().getLocation();
        if (slotLocation == null || slotLocation.getAddress() == null) return false;
        
        return slotLocation.getAddress().toLowerCase().contains(location.toLowerCase());
    }

    private boolean filterByInsurance(AppointmentSlot slot, Boolean insuranceAccepted) {
        if (insuranceAccepted == null) return true;
        
        ProviderAvailability.Pricing pricing = slot.getAvailability().getPricing();
        if (pricing == null) return false;
        
        return pricing.getInsuranceAccepted().equals(insuranceAccepted);
    }

    private boolean filterByPrice(AppointmentSlot slot, BigDecimal maxPrice) {
        if (maxPrice == null) return true;
        
        ProviderAvailability.Pricing pricing = slot.getAvailability().getPricing();
        if (pricing == null || pricing.getBaseFee() == null) return false;
        
        return pricing.getBaseFee().compareTo(maxPrice) <= 0;
    }

    private AvailabilitySearchResponse.AvailableSlotDto convertToAvailableSlotDto(AppointmentSlot slot) {
        AvailabilitySearchResponse.AvailableSlotDto dto = new AvailabilitySearchResponse.AvailableSlotDto();
        dto.setSlotId(slot.getId());
        dto.setDate(slot.getSlotStartTime().toLocalDate());
        dto.setStartTime(slot.getSlotStartTime().toLocalTime());
        dto.setEndTime(slot.getSlotEndTime().toLocalTime());
        dto.setAppointmentType(ProviderAvailability.AppointmentType.valueOf(slot.getAppointmentType()));

        ProviderAvailability availability = slot.getAvailability();
        if (availability.getLocation() != null) {
            dto.setLocation(new AvailabilitySearchResponse.LocationDto(
                availability.getLocation().getType(),
                availability.getLocation().getAddress(),
                availability.getLocation().getRoomNumber()
            ));
        }

        if (availability.getPricing() != null) {
            dto.setPricing(new AvailabilitySearchResponse.PricingDto(
                availability.getPricing().getBaseFee(),
                availability.getPricing().getInsuranceAccepted(),
                availability.getPricing().getCurrency()
            ));
        }

        dto.setSpecialRequirements(availability.getSpecialRequirements());

        return dto;
    }

    private AvailabilitySearchResponse.SearchCriteriaDto convertToSearchCriteria(AvailabilitySearchRequest request) {
        return new AvailabilitySearchResponse.SearchCriteriaDto(
            request.getDate(),
            request.getSpecialization(),
            request.getLocation(),
            request.getAppointmentType(),
            request.getInsuranceAccepted(),
            request.getMaxPrice(),
            request.getTimezone()
        );
    }
} 