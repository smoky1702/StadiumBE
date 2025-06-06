package com.example.bookingStadium.dto.request.Stadium;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;

/**
 * Request DTO for setting stadium pricing multipliers by time slots
 * Time slots:
 * - Morning: 6:00 - 12:00
 * - Afternoon: 12:00 - 18:00  
 * - Evening: 18:00 - 22:00
 * - Night: 22:00 - 6:00
 */
public class StadiumPricingRequest {
    // stadiumId is set from path parameter, not from JSON body
    // No validation needed here as it comes from URL path
    private String stadiumId;
    
    @DecimalMin(value = "0.1", message = "MULTIPLIER_INVALID_MIN")
    @DecimalMax(value = "10.0", message = "MULTIPLIER_INVALID_MAX")
    private double morningMultiplier = 1.0; // 6:00 - 12:00
    
    @DecimalMin(value = "0.1", message = "MULTIPLIER_INVALID_MIN")
    @DecimalMax(value = "10.0", message = "MULTIPLIER_INVALID_MAX")
    private double afternoonMultiplier = 1.0; // 12:00 - 18:00
    
    @DecimalMin(value = "0.1", message = "MULTIPLIER_INVALID_MIN")
    @DecimalMax(value = "10.0", message = "MULTIPLIER_INVALID_MAX")
    private double eveningMultiplier = 1.0; // 18:00 - 22:00
    
    @DecimalMin(value = "0.1", message = "MULTIPLIER_INVALID_MIN")
    @DecimalMax(value = "10.0", message = "MULTIPLIER_INVALID_MAX")
    private double nightMultiplier = 1.0; // 22:00 - 6:00z

    // Constructors
    public StadiumPricingRequest() {}

    public StadiumPricingRequest(String stadiumId, double morningMultiplier, 
                               double afternoonMultiplier, double eveningMultiplier, 
                               double nightMultiplier) {
        this.stadiumId = stadiumId;
        this.morningMultiplier = morningMultiplier;
        this.afternoonMultiplier = afternoonMultiplier;
        this.eveningMultiplier = eveningMultiplier;
        this.nightMultiplier = nightMultiplier;
    }

    // Getters and Setters
    public String getStadiumId() {
        return stadiumId;
    }

    public void setStadiumId(String stadiumId) {
        this.stadiumId = stadiumId;
    }

    public double getMorningMultiplier() {
        return morningMultiplier;
    }

    public void setMorningMultiplier(double morningMultiplier) {
        this.morningMultiplier = morningMultiplier;
    }

    public double getAfternoonMultiplier() {
        return afternoonMultiplier;
    }

    public void setAfternoonMultiplier(double afternoonMultiplier) {
        this.afternoonMultiplier = afternoonMultiplier;
    }

    public double getEveningMultiplier() {
        return eveningMultiplier;
    }

    public void setEveningMultiplier(double eveningMultiplier) {
        this.eveningMultiplier = eveningMultiplier;
    }

    public double getNightMultiplier() {
        return nightMultiplier;
    }

    public void setNightMultiplier(double nightMultiplier) {
        this.nightMultiplier = nightMultiplier;
    }
}
