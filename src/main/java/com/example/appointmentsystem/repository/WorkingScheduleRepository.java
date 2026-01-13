package com.example.appointmentsystem.repository;

import com.example.appointmentsystem.entity.WorkingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.Optional;

public interface WorkingScheduleRepository extends JpaRepository<WorkingSchedule, Long> {
    Optional<WorkingSchedule> findByDayOfWeek(DayOfWeek dayOfWeek);
}
