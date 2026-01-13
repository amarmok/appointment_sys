package com.example.appointmentsystem.controller;

import com.example.appointmentsystem.dto.WorkingScheduleRequest;
import com.example.appointmentsystem.entity.WorkingSchedule;
import com.example.appointmentsystem.service.WorkingScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/working-schedules")
@RequiredArgsConstructor
public class WorkingScheduleController {

    private final WorkingScheduleService workingScheduleService;

    @GetMapping
    public List<WorkingSchedule> list() {
        return workingScheduleService.listAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public WorkingSchedule create(@Valid @RequestBody WorkingScheduleRequest request) {
        return workingScheduleService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public WorkingSchedule update(
            @PathVariable Long id,
            @Valid @RequestBody WorkingScheduleRequest request
    ) {
        return workingScheduleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        workingScheduleService.delete(id);
    }
}
