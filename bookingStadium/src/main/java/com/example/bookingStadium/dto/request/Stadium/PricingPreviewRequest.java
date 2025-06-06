package com.example.bookingStadium.dto.request.Stadium;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

/**
 * Request DTO for getting pricing preview for a specific time range
 * Used to calculate total pricing for a booking period with detailed hourly breakdown
 */
public class PricingPreviewRequest {
    // stadiumId is set from path parameter, not from JSON body
    // No validation needed here as it comes from URL path
    private String stadiumId;
    
    @NotNull(message = "START_TIME_REQUIRED")
    private LocalTime startTime;
    
    @NotNull(message = "END_TIME_REQUIRED")
    private LocalTime endTime;

    // Constructors
    public PricingPreviewRequest() {}

    public PricingPreviewRequest(String stadiumId, LocalTime startTime, LocalTime endTime) {
        this.stadiumId = stadiumId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public String getStadiumId() {
        return stadiumId;
    }

    public void setStadiumId(String stadiumId) {
        this.stadiumId = stadiumId;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
