package com.example.appointmentsystem.controller;

import com.example.appointmentsystem.dto.SuggestionResponse;
import com.example.appointmentsystem.service.SmartSchedulingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class SchedulingController {

    private final SmartSchedulingService schedulingService;

    @GetMapping("/suggest")
    public SuggestionResponse getSuggestion(
            @RequestParam Long serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate preferredDate,
            @RequestParam(required = false, defaultValue = "") String preference) {

        return schedulingService.getSuggestedTime(serviceId, preferredDate, preference);
    }
}
