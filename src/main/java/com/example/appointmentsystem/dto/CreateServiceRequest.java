package com.example.appointmentsystem.dto;

public record CreateServiceRequest(
        String name,
        Integer durationMinutes,
        Double price,
        Long staffId
) {}
