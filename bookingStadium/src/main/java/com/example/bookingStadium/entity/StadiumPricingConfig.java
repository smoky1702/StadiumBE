package com.example.bookingStadium.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Stadium_Pricing_Config")
public class StadiumPricingConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "pricing_config_id")
    private String id;
    
    @Column(name = "stadium_id", nullable = false, unique = true)
    private String stadiumId;
    
    // Time slot multipliers (6:00-12:00)
    @Column(name = "morning_multiplier", nullable = false)
    private Double morningMultiplier = 1.0;
    
    // (12:00-18:00)
    @Column(name = "afternoon_multiplier", nullable = false) 
    private Double afternoonMultiplier = 1.0;
    
    // (18:00-22:00)
    @Column(name = "evening_multiplier", nullable = false)
    private Double eveningMultiplier = 1.0;
    
    // (22:00-6:00)
    @Column(name = "night_multiplier", nullable = false)
    private Double nightMultiplier = 1.0;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;
    
    // Constructors
    public StadiumPricingConfig() {}
    
    public StadiumPricingConfig(String stadiumId, Double morningMultiplier, Double afternoonMultiplier, 
                               Double eveningMultiplier, Double nightMultiplier) {
        this.stadiumId = stadiumId;
        this.morningMultiplier = morningMultiplier;
        this.afternoonMultiplier = afternoonMultiplier;
        this.eveningMultiplier = eveningMultiplier;
        this.nightMultiplier = nightMultiplier;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getStadiumId() { return stadiumId; }
    public void setStadiumId(String stadiumId) { this.stadiumId = stadiumId; }
    
    public Double getMorningMultiplier() { return morningMultiplier; }
    public void setMorningMultiplier(Double morningMultiplier) { this.morningMultiplier = morningMultiplier; }
    
    public Double getAfternoonMultiplier() { return afternoonMultiplier; }
    public void setAfternoonMultiplier(Double afternoonMultiplier) { this.afternoonMultiplier = afternoonMultiplier; }
    
    public Double getEveningMultiplier() { return eveningMultiplier; }
    public void setEveningMultiplier(Double eveningMultiplier) { this.eveningMultiplier = eveningMultiplier; }
    
    public Double getNightMultiplier() { return nightMultiplier; }
    public void setNightMultiplier(Double nightMultiplier) { this.nightMultiplier = nightMultiplier; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
    
    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
