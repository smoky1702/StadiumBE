package com.example.bookingStadium.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "Stadium_Time_Boundaries")
public class StadiumTimeBoundaries {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;
    
    @Column(name = "stadium_id", nullable = false, unique = true)
    private String stadiumId;
    
    // Morning period boundaries
    @Column(name = "morning_start", nullable = false)
    private LocalTime morningStart;
    
    @Column(name = "morning_end", nullable = false)
    private LocalTime morningEnd;
    
    // Afternoon period boundaries
    @Column(name = "afternoon_start", nullable = false)
    private LocalTime afternoonStart;
    
    @Column(name = "afternoon_end", nullable = false)
    private LocalTime afternoonEnd;
    
    // Evening period boundaries
    @Column(name = "evening_start", nullable = false)
    private LocalTime eveningStart;
    
    @Column(name = "evening_end", nullable = false)
    private LocalTime eveningEnd;
    
    // Night period boundaries
    @Column(name = "night_start", nullable = false)
    private LocalTime nightStart;
    
    @Column(name = "night_end", nullable = false)
    private LocalTime nightEnd;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;
    
    // Constructors
    public StadiumTimeBoundaries() {}
    
    public StadiumTimeBoundaries(String stadiumId) {
        this.stadiumId = stadiumId;
        // Set default boundaries
        this.morningStart = LocalTime.of(6, 0);
        this.morningEnd = LocalTime.of(12, 0);
        this.afternoonStart = LocalTime.of(12, 0);
        this.afternoonEnd = LocalTime.of(18, 0);
        this.eveningStart = LocalTime.of(18, 0);
        this.eveningEnd = LocalTime.of(22, 0);
        this.nightStart = LocalTime.of(22, 0);
        this.nightEnd = LocalTime.of(6, 0);
        this.isActive = true;
    }
    
    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
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
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }
    
    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
}
