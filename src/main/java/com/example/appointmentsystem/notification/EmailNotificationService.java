package com.example.appointmentsystem.notification;

import com.example.appointmentsystem.entity.Appointment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.host:}")
    private String mailHost;

    @Value("${spring.mail.username:}")
    private String mailFrom;

    public void sendAppointmentCreated(Appointment appointment) {
        String subject = "Appointment Created";
        String body = "Your appointment for "
                + appointment.getService().getName()
                + " is scheduled on "
                + appointment.getAppointmentDate()
                + " from "
                + appointment.getStartTime()
                + " to "
                + appointment.getEndTime()
                + ". Status: "
                + appointment.getStatus();
        send(appointment.getCustomer().getEmail(), subject, body);
    }

    public void sendAppointmentStatusChanged(Appointment appointment) {
        String subject = "Appointment Status Updated";
        String body = "Your appointment for "
                + appointment.getService().getName()
                + " on "
                + appointment.getAppointmentDate()
                + " is now "
                + appointment.getStatus()
                + ".";
        send(appointment.getCustomer().getEmail(), subject, body);
    }

    private void send(String to, String subject, String body) {
        if (mailHost == null || mailHost.isBlank()) {
            log.warn("Email notifications skipped: SMTP is not configured.");
            return;
        }
        if (to == null || to.isBlank()) {
            log.warn("Email notifications skipped: recipient email is missing.");
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        if (mailFrom != null && !mailFrom.isBlank()) {
            message.setFrom(mailFrom);
        }
        try {
            mailSender.send(message);
        } catch (MailException ex) {
            log.warn("Failed to send email notification to {}", to, ex);
        }
    }
}
