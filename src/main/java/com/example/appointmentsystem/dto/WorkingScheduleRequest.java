package com.example.appointmentsystem.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record WorkingScheduleRequest(
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        boolean isHoliday
) {}
