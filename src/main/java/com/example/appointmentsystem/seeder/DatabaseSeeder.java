package com.example.appointmentsystem.seeder;

import com.example.appointmentsystem.entity.*;
import com.example.appointmentsystem.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!prod") // Run only in non-production environments
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final WorkingScheduleRepository workingScheduleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting database seeding...");
        seedUsers();
        seedServices();
        seedWorkingSchedules();
        log.info("Database seeding completed successfully!");
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            List<User> users = Arrays.asList(
                    User.builder()
                            .fullName("Admin User")
                            .email("admin@example.com")
                            .password(passwordEncoder.encode("admin123"))
                            .role(Role.ADMIN)
                            .createdAt(LocalDateTime.now())
                            .build(),

                    User.builder()
                            .fullName("John Doe - Hair Stylist")
                            .email("john.stylist@example.com")
                            .password(passwordEncoder.encode("staff123"))
                            .role(Role.STAFF)
                            .createdAt(LocalDateTime.now())
                            .build(),

                    User.builder()
                            .fullName("Jane Smith - Nail Technician")
                            .email("jane.tech@example.com")
                            .password(passwordEncoder.encode("staff123"))
                            .role(Role.STAFF)
                            .createdAt(LocalDateTime.now())
                            .build(),

                    User.builder()
                            .fullName("Michael Johnson")
                            .email("michael@example.com")
                            .password(passwordEncoder.encode("customer123"))
                            .role(Role.CUSTOMER)
                            .createdAt(LocalDateTime.now())
                            .build(),

                    User.builder()
                            .fullName("Sarah Williams")
                            .email("sarah@example.com")
                            .password(passwordEncoder.encode("customer123"))
                            .role(Role.CUSTOMER)
                            .createdAt(LocalDateTime.now())
                            .build()
            );

            userRepository.saveAll(users);
            log.info("Seeded {} users", users.size());
        }
    }

    private void seedServices() {
        if (serviceRepository.count() == 0) {
            // Get staff users
            User hairStylist = userRepository.findByEmail("john.stylist@example.com")
                    .orElse(null);
            User nailTechnician = userRepository.findByEmail("jane.tech@example.com")
                    .orElse(null);
            if (hairStylist == null || nailTechnician == null) {
                log.warn("Skipping service seeding because staff users are missing.");
                return;
            }

            List<Service> services = Arrays.asList(
                    Service.builder()
                            .name("Hair Cut")
                            .durationMinutes(30)
                            .price(25.0)
                            .staff(hairStylist)
                            .build(),

                    Service.builder()
                            .name("Hair Coloring")
                            .durationMinutes(120)
                            .price(80.0)
                            .staff(hairStylist)
                            .build(),

                    Service.builder()
                            .name("Basic Manicure")
                            .durationMinutes(45)
                            .price(20.0)
                            .staff(nailTechnician)
                            .build(),

                    Service.builder()
                            .name("Gel Nails")
                            .durationMinutes(90)
                            .price(50.0)
                            .staff(nailTechnician)
                            .build(),

                    Service.builder()
                            .name("Beard Trim")
                            .durationMinutes(20)
                            .price(15.0)
                            .staff(hairStylist)
                            .build()
            );

            serviceRepository.saveAll(services);
            log.info("Seeded {} services", services.size());
        }
    }

    private void seedWorkingSchedules() {
        if (workingScheduleRepository.count() == 0) {
            List<WorkingSchedule> schedules = Arrays.asList(
                    // Monday to Friday working hours
                    WorkingSchedule.builder()
                            .dayOfWeek(DayOfWeek.MONDAY)
                            .startTime(LocalTime.of(9, 0))
                            .endTime(LocalTime.of(18, 0))
                            .isHoliday(false)
                            .build(),

                    WorkingSchedule.builder()
                            .dayOfWeek(DayOfWeek.TUESDAY)
                            .startTime(LocalTime.of(9, 0))
                            .endTime(LocalTime.of(18, 0))
                            .isHoliday(false)
                            .build(),

                    WorkingSchedule.builder()
                            .dayOfWeek(DayOfWeek.WEDNESDAY)
                            .startTime(LocalTime.of(9, 0))
                            .endTime(LocalTime.of(18, 0))
                            .isHoliday(false)
                            .build(),

                    WorkingSchedule.builder()
                            .dayOfWeek(DayOfWeek.THURSDAY)
                            .startTime(LocalTime.of(9, 0))
                            .endTime(LocalTime.of(20, 0)) // Extended hours
                            .isHoliday(false)
                            .build(),

                    WorkingSchedule.builder()
                            .dayOfWeek(DayOfWeek.FRIDAY)
                            .startTime(LocalTime.of(9, 0))
                            .endTime(LocalTime.of(18, 0))
                            .isHoliday(false)
                            .build(),

                    // Saturday - shorter hours
                    WorkingSchedule.builder()
                            .dayOfWeek(DayOfWeek.SATURDAY)
                            .startTime(LocalTime.of(10, 0))
                            .endTime(LocalTime.of(16, 0))
                            .isHoliday(false)
                            .build(),

                    // Sunday - holiday/closed
                    WorkingSchedule.builder()
                            .dayOfWeek(DayOfWeek.SUNDAY)
                            .startTime(LocalTime.of(0, 0))
                            .endTime(LocalTime.of(0, 0))
                            .isHoliday(true)
                            .build()
            );

            workingScheduleRepository.saveAll(schedules);
            log.info("Seeded {} working schedules", schedules.size());
        }
    }
}
