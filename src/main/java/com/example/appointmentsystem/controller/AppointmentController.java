package com.example.appointmentsystem.controller;

import com.example.appointmentsystem.dto.CreateAppointmentRequest;
import com.example.appointmentsystem.dto.SuggestionResponse;
import com.example.appointmentsystem.entity.*;
import com.example.appointmentsystem.service.AppointmentService;
import com.example.appointmentsystem.service.SmartSchedulingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;
    private final SmartSchedulingService smartSchedulingService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public Appointment create(@Valid @RequestBody CreateAppointmentRequest r) {
        return service.create(r.customerId(), r.serviceId(), r.date(), r.startTime());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<Appointment> listMyAppointments() {
        return service.listForCurrentCustomer();
    }

    @GetMapping("/staff")
    @PreAuthorize("hasRole('STAFF')")
    public List<Appointment> listStaffAppointments() {
        return service.listForCurrentStaff();
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Appointment cancel(@PathVariable Long id) {
        return service.cancel(id);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Appointment updateStatus(@PathVariable Long id, @RequestParam AppointmentStatus status) {
        return service.updateStatus(id, status);
    }

    @GetMapping("/suggest")
    public ResponseEntity<SuggestionResponse> getSuggestedTime(
            @RequestParam Long serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate,
            @RequestParam(required = false, defaultValue = "") String customerPreference) {

        SuggestionResponse suggestion = smartSchedulingService.getSuggestedTime(
                serviceId, targetDate, customerPreference);
        return ResponseEntity.ok(suggestion);
    }
}
