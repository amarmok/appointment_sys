package com.example.appointmentsystem.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateAppointmentRequest(
        Long customerId,
        Long serviceId,
        LocalDate date,
        LocalTime startTime
) {}
