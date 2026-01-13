package com.example.appointmentsystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.appointmentsystem.dto.SuggestionResponse;
import com.example.appointmentsystem.entity.*;
import com.example.appointmentsystem.exception.BusinessException;
import com.example.appointmentsystem.exception.RateLimitException;
import com.example.appointmentsystem.repository.AppointmentRepository;
import com.example.appointmentsystem.repository.ServiceRepository;
import com.example.appointmentsystem.repository.WorkingScheduleRepository;
import com.google.genai.Client;
import com.google.genai.errors.ApiException;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SmartSchedulingService {

    private final WorkingScheduleRepository workingScheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final ServiceRepository serviceRepository;

    // Key Method: Get a suggestion from Gemini

    public SuggestionResponse getSuggestedTime(Long serviceId, LocalDate targetDate, String customerPreference) {

        // 1. Fetch necessary data (YOUR EXISTING CODE)
        com.example.appointmentsystem.entity.Service requestedService = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new BusinessException("Service not found"));
        List<WorkingSchedule> weeklySchedule = workingScheduleRepository.findAll();
        List<Appointment> existingAppointments = appointmentRepository
                .findByAppointmentDateAndStatusNot(targetDate, AppointmentStatus.CANCELLED);

        // 2. Build the structured prompt
        String prompt = buildSchedulingPrompt(requestedService, weeklySchedule, existingAppointments, targetDate, customerPreference);

        try {
            // 3. Call Gemini
            Client client = new Client();
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.0-flash",
                    prompt,
                    null
            );

            // 4. Extract the text response
            String aiResponse = response.text();

            // 5. PARSE THE JSON RESPONSE (This was missing!)
            ObjectMapper mapper = new ObjectMapper();
            SuggestionResponse suggestion = mapper.readValue(aiResponse, SuggestionResponse.class);

            return suggestion; // Now returns the parsed object, not raw string

        } catch (ApiException e) {
            if (e.code() == 429) {
                throw new RateLimitException("Gemini quota exceeded. Please retry later.", e);
            }
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Gemini service is unavailable",
                    e
            );
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Gemini service is unavailable",
                    e
            );
        }
    }
    // YOUR buildSchedulingPrompt METHOD REMAINS EXACTLY THE SAME
    // It is already perfect. Do not change it.
    private String buildSchedulingPrompt( com.example.appointmentsystem.entity.Service service,
                                         List<WorkingSchedule> schedule,
                                         List<Appointment> appointments,
                                         LocalDate date,
                                         String preference) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        // Format working hours
        String scheduleText = schedule.stream()
                .map(ws -> String.format("- %s: %s to %s (Holiday: %s)",
                        ws.getDayOfWeek(),
                        ws.getStartTime().format(formatter),
                        ws.getEndTime().format(formatter),
                        ws.isHoliday()))
                .collect(Collectors.joining("\n"));

        // Format existing appointments
        String appointmentsText = appointments.stream()
                .map(apt -> String.format("- %s to %s for service '%s'",
                        apt.getStartTime().format(formatter),
                        apt.getEndTime().format(formatter),
                        apt.getService().getName()))
                .collect(Collectors.joining("\n"));

        // Construct the final prompt
        return String.format("""
            You are an intelligent scheduling assistant for a service business.
            
            ### CONTEXT AND RULES:
            1. **Service Requested**: '%s' (Duration: %d minutes).
            2. **Customer Preference Note**: "%s"
            3. **Target Date**: %s (%s).
            
            ### BUSINESS WORKING HOURS (Weekly Schedule):
            %s
            
            ### ALREADY BOOKED APPOINTMENTS on %s:
            %s
            
            ### YOUR TASK:
            Analyze the above information and suggest the **best available start time** for the new appointment.
            - It must fit within the working hours for the target day.
            - It must NOT overlap with any existing appointment.
            - Prefer times that match the customer's preference if possible.
            - If no good slot exists on the target date, suggest the **next best available date and time** within the next 7 days.
            - Consider standard breaks (e.g., lunch around 12:00-13:00) even if not explicitly blocked.
            
            ### RESPONSE FORMAT:
            Please provide your answer in a clear, concise JSON format:
            {
              "suggestedTime": "YYYY-MM-DDTHH:MM:SS",
              "alternativeTime": "YYYY-MM-DDTHH:MM:SS" (or null),
              "reasoning": "Brief explanation of your choice"
            }
            """,
                service.getName(),
                service.getDurationMinutes(),
                preference,
                date,
                date.getDayOfWeek(),
                scheduleText,
                date,
                appointmentsText.isEmpty() ? "No appointments booked yet." : appointmentsText
        );
    }
}
