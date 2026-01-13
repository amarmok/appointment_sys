package com.example.appointmentsystem.dto;

import lombok.Data;

@Data
public class SuggestionResponse {
    private String suggestedTime; // Format: "YYYY-MM-DDTHH:MM:SS"
    private String alternativeTime;
    private String reasoning;
}