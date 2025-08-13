package com.thinkitive.thinkemr.dto;

import com.thinkitive.thinkemr.entity.AppointmentSlot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSlotsResponse {

    private UUID providerId;
    private List<SlotDto> slots;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SlotDto {
        private UUID slotId;
        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;
        private AppointmentSlot.SlotStatus status;
        private String appointmentType;
        private Integer currentAppointments;
        private Integer maxAppointmentsPerSlot;
    }

    public static AvailableSlotsResponse fromSlots(UUID providerId, List<AppointmentSlot> slots) {
        List<SlotDto> slotDtos = slots.stream()
            .map(slot -> new SlotDto(
                slot.getId(),
                slot.getSlotStartTime().toLocalDate(),
                slot.getSlotStartTime().toLocalTime(),
                slot.getSlotEndTime().toLocalTime(),
                slot.getStatus(),
                slot.getAppointmentType(),
                slot.getAvailability().getCurrentAppointments(),
                slot.getAvailability().getMaxAppointmentsPerSlot()
            ))
            .toList();

        return new AvailableSlotsResponse(providerId, slotDtos);
    }
} 