package com.example.appointmentsystem.notification;

import com.example.appointmentsystem.entity.Appointment;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyAppointmentCreated(Appointment appointment) {
        String message =
                "Appointment #" + appointment.getId()
                        + " created for "
                        + appointment.getService().getName()
                        + " on "
                        + appointment.getAppointmentDate()
                        + " at "
                        + appointment.getStartTime();

        messagingTemplate.convertAndSend(
                "/topic/appointments",
                message
        );
    }

    public void notifyAppointmentStatusChanged(
            Long appointmentId,
            String newStatus
    ) {
        String message =
                "Appointment #" + appointmentId +
                        " status changed to " + newStatus;

        messagingTemplate.convertAndSend(
                "/topic/appointments",
                message
        );
    }
}
