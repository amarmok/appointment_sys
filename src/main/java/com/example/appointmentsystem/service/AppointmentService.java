package com.example.appointmentsystem.service;

import com.example.appointmentsystem.entity.*;
import com.example.appointmentsystem.exception.BusinessException;
import com.example.appointmentsystem.notification.EmailNotificationService;
import com.example.appointmentsystem.notification.NotificationService;
import com.example.appointmentsystem.repository.AppointmentRepository;
import com.example.appointmentsystem.repository.ServiceRepository;
import com.example.appointmentsystem.repository.UserRepository;
import com.example.appointmentsystem.repository.WorkingScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final ServiceRepository serviceRepo;
    private final UserRepository userRepo;
    private final WorkingScheduleRepository scheduleRepo;
    private final NotificationService notificationService;
    private final EmailNotificationService emailNotificationService;

    @Transactional
    public Appointment create(
            Long customerId,
            Long serviceId,
            java.time.LocalDate date,
            LocalTime start
    ) {

        User customer = getCurrentUser();
        if (customer.getRole() != Role.CUSTOMER) {
            throw new BusinessException("Only customers can book appointments");
        }
        if (customerId != null && !customer.getId().equals(customerId)) {
            throw new BusinessException("Cannot create appointment for another customer");
        }

        var service = serviceRepo.findById(serviceId)
                .orElseThrow(() -> new BusinessException("Service not found"));

        if (service.getStaff() == null || service.getStaff().getRole() != Role.STAFF) {
            throw new BusinessException("Service staff not available");
        }

        LocalTime end = start.plusMinutes(service.getDurationMinutes());

        DayOfWeek day = date.getDayOfWeek();
        var schedule = scheduleRepo.findByDayOfWeek(day)
                .orElseThrow(() -> new BusinessException("No schedule for this day"));

        if (schedule.isHoliday()) {
            throw new BusinessException("Holiday");
        }

        if (start.isBefore(schedule.getStartTime()) || end.isAfter(schedule.getEndTime())) {
            throw new BusinessException("Outside working hours");
        }

        boolean conflict = appointmentRepo.existsOverlap(
                service.getStaff().getId(),
                date,
                start,
                end,
                List.of(AppointmentStatus.PENDING, AppointmentStatus.APPROVED)
        );

        if (conflict) {
            throw new BusinessException("Time already booked");
        }

        Appointment saved = appointmentRepo.save(
                Appointment.builder()
                        .appointmentDate(date)
                        .startTime(start)
                        .endTime(end)
                        .status(AppointmentStatus.PENDING)
                        .customer(customer)
                        .service(service)
                        .build()
        );

        notificationService.notifyAppointmentCreated(saved);
        emailNotificationService.sendAppointmentCreated(saved);

        return saved;
    }

    @Transactional
    public Appointment updateStatus(Long id, AppointmentStatus status) {

        var appointment = appointmentRepo.findById(id)
                .orElseThrow(() -> new BusinessException("Appointment not found"));

        validateStatusTransition(appointment.getStatus(), status);
        appointment.setStatus(status);
        Appointment saved = appointmentRepo.save(appointment);

        notificationService.notifyAppointmentStatusChanged(
                saved.getId(),
                saved.getStatus().name()
        );
        emailNotificationService.sendAppointmentStatusChanged(saved);

        return saved;
    }

    @Transactional
    public Appointment cancel(Long appointmentId) {
        User customer = getCurrentUser();
        var appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new BusinessException("Appointment not found"));
        if (!appointment.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessException("Cannot cancel another customer's appointment");
        }
        validateStatusTransition(appointment.getStatus(), AppointmentStatus.CANCELLED);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment saved = appointmentRepo.save(appointment);
        notificationService.notifyAppointmentStatusChanged(
                saved.getId(),
                saved.getStatus().name()
        );
        emailNotificationService.sendAppointmentStatusChanged(saved);
        return saved;
    }

    public List<Appointment> listForCurrentCustomer() {
        User customer = getCurrentUser();
        return appointmentRepo.findByCustomerIdOrderByAppointmentDateAscStartTimeAsc(customer.getId());
    }

    public List<Appointment> listForCurrentStaff() {
        User staff = getCurrentUser();
        if (staff.getRole() != Role.STAFF) {
            throw new BusinessException("Only staff can view staff appointments");
        }
        return appointmentRepo.findByServiceStaffIdOrderByAppointmentDateAscStartTimeAsc(staff.getId());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("Unauthorized");
        }
        String email = authentication.getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));
    }

    private void validateStatusTransition(AppointmentStatus current, AppointmentStatus target) {
        if (current == AppointmentStatus.PENDING) {
            if (target == AppointmentStatus.APPROVED || target == AppointmentStatus.CANCELLED) {
                return;
            }
        } else if (current == AppointmentStatus.APPROVED) {
            if (target == AppointmentStatus.FINISHED || target == AppointmentStatus.CANCELLED) {
                return;
            }
        }
        throw new BusinessException("Invalid status transition from " + current + " to " + target);
    }
}
