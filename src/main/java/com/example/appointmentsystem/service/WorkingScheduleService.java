package com.example.appointmentsystem.service;

import com.example.appointmentsystem.dto.WorkingScheduleRequest;
import com.example.appointmentsystem.entity.WorkingSchedule;
import com.example.appointmentsystem.exception.BusinessException;
import com.example.appointmentsystem.repository.WorkingScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class WorkingScheduleService {

    private final WorkingScheduleRepository repository;

    public List<WorkingSchedule> listAll() {
        return repository.findAll();
    }

    @Transactional
    public WorkingSchedule create(WorkingScheduleRequest request) {
        repository.findByDayOfWeek(request.dayOfWeek())
                .ifPresent(existing -> {
                    throw new BusinessException("Schedule already exists for " + request.dayOfWeek());
                });
        WorkingSchedule schedule = WorkingSchedule.builder()
                .dayOfWeek(request.dayOfWeek())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .isHoliday(request.isHoliday())
                .build();
        return repository.save(schedule);
    }

    @Transactional
    public WorkingSchedule update(Long id, WorkingScheduleRequest request) {
        WorkingSchedule existing = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Working schedule not found"));
        repository.findByDayOfWeek(request.dayOfWeek())
                .filter(schedule -> !schedule.getId().equals(id))
                .ifPresent(schedule -> {
                    throw new BusinessException("Schedule already exists for " + request.dayOfWeek());
                });
        existing.setDayOfWeek(request.dayOfWeek());
        existing.setStartTime(request.startTime());
        existing.setEndTime(request.endTime());
        existing.setHoliday(request.isHoliday());
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new BusinessException("Working schedule not found");
        }
        repository.deleteById(id);
    }
}
