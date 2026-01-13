package com.example.appointmentsystem.repository;

import com.example.appointmentsystem.entity.Appointment;
import com.example.appointmentsystem.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // ADD THIS METHOD - It's called in SmartSchedulingService
    List<Appointment> findByAppointmentDateAndStatusNot(
            @Param("date") LocalDate date,
            @Param("status") AppointmentStatus status
    );

    List<Appointment> findByCustomerIdOrderByAppointmentDateAscStartTimeAsc(Long customerId);

    List<Appointment> findByServiceStaffIdOrderByAppointmentDateAscStartTimeAsc(Long staffId);

    // Your existing method for checking overlaps
    @Query("""
        select count(a) > 0 from Appointment a
        where a.service.staff.id = :staffId
          and a.appointmentDate = :date
          and a.status in :statuses
          and (:start < a.endTime and :end > a.startTime)
    """)
    boolean existsOverlap(
            Long staffId,
            LocalDate date,
            LocalTime start,
            LocalTime end,
            List<AppointmentStatus> statuses
    );
}
