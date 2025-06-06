package com.example.bookingStadium.dto.request.Stadium;

import java.time.LocalTime;

/**
 * Request DTO for customizing time boundaries of 4 fixed time slots
 * Much simpler than custom time slots - just change the boundaries
 */
public class CustomTimeBoundariesRequest {
    
    private String stadiumId;
    
    // Morning period boundaries
    private LocalTime morningStart = LocalTime.of(6, 0);  // Default 6:00
    private LocalTime morningEnd = LocalTime.of(12, 0);   // Default 12:00
    
    // Afternoon period boundaries
    private LocalTime afternoonStart = LocalTime.of(12, 0); // Default 12:00
    private LocalTime afternoonEnd = LocalTime.of(18, 0);   // Default 18:00
    
    // Evening period boundaries  
    private LocalTime eveningStart = LocalTime.of(18, 0);   // Default 18:00
    private LocalTime eveningEnd = LocalTime.of(22, 0);     // Default 22:00
    
    // Night period boundaries (can cross midnight)
    private LocalTime nightStart = LocalTime.of(22, 0);     // Default 22:00
    private LocalTime nightEnd = LocalTime.of(6, 0);       // Default 6:00 (next day)
    
    // Multipliers for each period
    private double morningMultiplier = 0.8;
    private double afternoonMultiplier = 1.0;
    private double eveningMultiplier = 1.5;
    private double nightMultiplier = 0.6;
    
    // Constructors
    public CustomTimeBoundariesRequest() {}
    
    public CustomTimeBoundariesRequest(String stadiumId) {
        this.stadiumId = stadiumId;
    }
    
    // Getters and Setters
    public String getStadiumId() {
        return stadiumId;
    }
    
    public void setStadiumId(String stadiumId) {
        this.stadiumId = stadiumId;
    }
    
    public LocalTime getMorningStart() {
        return morningStart;
    }
    
    public void setMorningStart(LocalTime morningStart) {
        this.morningStart = morningStart;
    }
    
    public LocalTime getMorningEnd() {
        return morningEnd;
    }
    
    public void setMorningEnd(LocalTime morningEnd) {
        this.morningEnd = morningEnd;
    }
    
    public LocalTime getAfternoonStart() {
        return afternoonStart;
    }
    
    public void setAfternoonStart(LocalTime afternoonStart) {
        this.afternoonStart = afternoonStart;
    }
    
    public LocalTime getAfternoonEnd() {
        return afternoonEnd;
    }
    
    public void setAfternoonEnd(LocalTime afternoonEnd) {
        this.afternoonEnd = afternoonEnd;
    }
    
    public LocalTime getEveningStart() {
        return eveningStart;
    }
    
    public void setEveningStart(LocalTime eveningStart) {
        this.eveningStart = eveningStart;
    }
    
    public LocalTime getEveningEnd() {
        return eveningEnd;
    }
    
    public void setEveningEnd(LocalTime eveningEnd) {
        this.eveningEnd = eveningEnd;
    }
    
    public LocalTime getNightStart() {
        return nightStart;
    }
    
    public void setNightStart(LocalTime nightStart) {
        this.nightStart = nightStart;
    }
    
    public LocalTime getNightEnd() {
        return nightEnd;
    }
    
    public void setNightEnd(LocalTime nightEnd) {
        this.nightEnd = nightEnd;
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
    
    /**
     * Validate that time boundaries don't overlap (optional validation)
     */
    public boolean isValid() {
        // Basic validation - morning end should equal afternoon start, etc.
        return morningEnd.equals(afternoonStart) && 
               afternoonEnd.equals(eveningStart) && 
               eveningEnd.equals(nightStart);
    }
}
