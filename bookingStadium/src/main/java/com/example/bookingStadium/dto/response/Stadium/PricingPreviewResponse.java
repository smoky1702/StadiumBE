package com.example.bookingStadium.dto.response.Stadium;

import java.time.LocalTime;
import java.util.List;

public class PricingPreviewResponse {
    private String stadiumId;
    private String stadiumName;
    private double basePrice;
    private LocalTime startTime;
    private LocalTime endTime;
    private double totalHours;
    private double totalPrice;
    private double averageMultiplier;
    private List<HourlyPriceBreakdown> hourlyBreakdown;

    // Constructors
    public PricingPreviewResponse() {}

    public PricingPreviewResponse(String stadiumId, String stadiumName, double basePrice,
                                LocalTime startTime, LocalTime endTime, double totalHours,
                                double totalPrice, double averageMultiplier,
                                List<HourlyPriceBreakdown> hourlyBreakdown) {
        this.stadiumId = stadiumId;
        this.stadiumName = stadiumName;
        this.basePrice = basePrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalHours = totalHours;
        this.totalPrice = totalPrice;
        this.averageMultiplier = averageMultiplier;
        this.hourlyBreakdown = hourlyBreakdown;
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

    public double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(double totalHours) {
        this.totalHours = totalHours;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getAverageMultiplier() {
        return averageMultiplier;
    }

    public void setAverageMultiplier(double averageMultiplier) {
        this.averageMultiplier = averageMultiplier;
    }

    public List<HourlyPriceBreakdown> getHourlyBreakdown() {
        return hourlyBreakdown;
    }

    public void setHourlyBreakdown(List<HourlyPriceBreakdown> hourlyBreakdown) {
        this.hourlyBreakdown = hourlyBreakdown;
    }

    // Inner class for hourly breakdown
    public static class HourlyPriceBreakdown {
        private int hour;
        private String timeSlot;
        private double multiplier;
        private double pricePerHour;
        private double minutes;

        public HourlyPriceBreakdown() {}

        public HourlyPriceBreakdown(int hour, String timeSlot, double multiplier, 
                                  double pricePerHour, double minutes) {
            this.hour = hour;
            this.timeSlot = timeSlot;
            this.multiplier = multiplier;
            this.pricePerHour = pricePerHour;
            this.minutes = minutes;
        }

        // Getters and Setters
        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public String getTimeSlot() {
            return timeSlot;
        }

        public void setTimeSlot(String timeSlot) {
            this.timeSlot = timeSlot;
        }

        public double getMultiplier() {
            return multiplier;
        }

        public void setMultiplier(double multiplier) {
            this.multiplier = multiplier;
        }

        public double getPricePerHour() {
            return pricePerHour;
        }

        public void setPricePerHour(double pricePerHour) {
            this.pricePerHour = pricePerHour;
        }

        public double getMinutes() {
            return minutes;
        }

        public void setMinutes(double minutes) {
            this.minutes = minutes;
        }
    }
}
