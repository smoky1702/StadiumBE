package com.example.bookingStadium.dto.request.Stadium;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import java.util.Map;

/**
 * Request DTO for setting hourly pricing for a stadium
 * Converts hourly pricing to time slot averages for storage in database
 * 
 * Supports two modes:
 * 1. Multipliers mode: hourly multipliers applied to base price
 * 2. Absolute prices mode: specific prices for each hour
 */
public class HourlyPricingRequest {
    // stadiumId is set from path parameter, not from JSON body
    // No validation needed here as it comes from URL path
    private String stadiumId;
    
    // Map of hour (0-23) to multiplier value
    @Size(min = 1, max = 24, message = "HOURLY_PRICING_SIZE_INVALID")
    private Map<Integer, @DecimalMin(value = "0.1", message = "MULTIPLIER_INVALID_MIN") Double> hourlyMultipliers;
    
    // Alternative: specific prices for specific hours
    private Map<Integer, @DecimalMin(value = "0.0", message = "HOURLY_PRICE_NEGATIVE") Double> hourlyPrices;
    
    // Flag to determine whether to use multipliers or absolute prices
    private boolean useMultipliers = true;

    // Constructors
    public HourlyPricingRequest() {}

    public HourlyPricingRequest(String stadiumId, Map<Integer, Double> hourlyMultipliers) {
        this.stadiumId = stadiumId;
        this.hourlyMultipliers = hourlyMultipliers;
        this.useMultipliers = true;
    }

    public HourlyPricingRequest(String stadiumId, Map<Integer, Double> hourlyPrices, boolean useMultipliers) {
        this.stadiumId = stadiumId;
        if (useMultipliers) {
            this.hourlyMultipliers = hourlyPrices;
        } else {
            this.hourlyPrices = hourlyPrices;
        }
        this.useMultipliers = useMultipliers;
    }

    // Getters and Setters
    public String getStadiumId() {
        return stadiumId;
    }

    public void setStadiumId(String stadiumId) {
        this.stadiumId = stadiumId;
    }

    public Map<Integer, Double> getHourlyMultipliers() {
        return hourlyMultipliers;
    }

    public void setHourlyMultipliers(Map<Integer, Double> hourlyMultipliers) {
        this.hourlyMultipliers = hourlyMultipliers;
    }

    public Map<Integer, Double> getHourlyPrices() {
        return hourlyPrices;
    }

    public void setHourlyPrices(Map<Integer, Double> hourlyPrices) {
        this.hourlyPrices = hourlyPrices;
    }

    public boolean isUseMultipliers() {
        return useMultipliers;
    }

    public void setUseMultipliers(boolean useMultipliers) {
        this.useMultipliers = useMultipliers;
    }

    /**
     * Get the active pricing map based on the mode
     * @return Map of hour to price/multiplier
     */
    public Map<Integer, Double> getActivePricingMap() {
        return useMultipliers ? hourlyMultipliers : hourlyPrices;
    }
}
