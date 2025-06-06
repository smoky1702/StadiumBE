package com.example.bookingStadium.dto.response.Stadium;

import java.time.LocalDateTime;

/**
 * Response DTO for stadium pricing configuration
 * Contains time slot multipliers and metadata about pricing settings
 */
public class StadiumPricingResponse {
    private String stadiumId;
    private String stadiumName;
    private double basePrice;
    private double morningMultiplier;   // 6:00 - 12:00
    private double afternoonMultiplier; // 12:00 - 18:00
    private double eveningMultiplier;   // 18:00 - 22:00
    private double nightMultiplier;     // 22:00 - 6:00
    private LocalDateTime lastUpdated;
    private boolean isActive;

    // Default constructor
    public StadiumPricingResponse() {}

    // Constructor with all fields
    public StadiumPricingResponse(String stadiumId, String stadiumName, double basePrice,
                                double morningMultiplier, double afternoonMultiplier,
                                double eveningMultiplier, double nightMultiplier,
                                LocalDateTime lastUpdated, boolean isActive) {
        this.stadiumId = stadiumId;
        this.stadiumName = stadiumName;
        this.basePrice = basePrice;
        this.morningMultiplier = morningMultiplier;
        this.afternoonMultiplier = afternoonMultiplier;
        this.eveningMultiplier = eveningMultiplier;
        this.nightMultiplier = nightMultiplier;
        this.lastUpdated = lastUpdated;
        this.isActive = isActive;
    }

    // Constructor without timestamps and status (for simple responses)
    public StadiumPricingResponse(String stadiumId, String stadiumName, double basePrice,
                                double morningMultiplier, double afternoonMultiplier,
                                double eveningMultiplier, double nightMultiplier) {
        this.stadiumId = stadiumId;
        this.stadiumName = stadiumName;
        this.basePrice = basePrice;
        this.morningMultiplier = morningMultiplier;
        this.afternoonMultiplier = afternoonMultiplier;
        this.eveningMultiplier = eveningMultiplier;
        this.nightMultiplier = nightMultiplier;
        this.lastUpdated = LocalDateTime.now();
        this.isActive = true;
    }

    // Getters and Setters
    public String getStadiumId() {
        return stadiumId;
    }

    public void setStadiumId(String stadiumId) {
        this.stadiumId = stadiumId;
    }

    public String getStadiumName() {
        return stadiumName;
    }

    public void setStadiumName(String stadiumName) {
        this.stadiumName = stadiumName;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
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

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
